package org.yyy.doorproxy;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.function.Function;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.yyy.proxy.common.Command;
import com.yyy.proxy.common.client.ErrorResponseCommand;
import com.yyy.proxy.common.client.RegisterCommand;
import com.yyy.proxy.common.server.DoorRequestCommand;

@Service
public class DoorProxy implements Runnable {

	private static Logger logger = LoggerFactory.getLogger(DoorProxy.class);

	@Value("${proxy.reconnect.delayInSecs}")
	private int reconDelaySecs = 120;

	@Value("${proxy.keepAliveInSecs}")
	private int keepAliveInSecs = 120;

	@Value("${proxy.server.host}")
	private String host;
	@Value("${proxy.server.port}")
	private int port;
	@Value("${proxy.trustStore.path}")
	private String keyStorePath;
	@Value("${proxy.trustStore.password}")
	private String ksPass = "changeme";

	@Value("${proxy.doorSecrets}")
	private String secrets;

	private SSLSocketFactory ssf;
	SSLSocket socket;
	BufferedWriter socketOut;

	@Autowired
	Function<String, CommandExecutor> executorFac;

	private Thread t;

	private DoorCommandReceiver receiver;

	private volatile boolean isRunning = true;
	private volatile boolean sendKeepAlive = true;

	private volatile boolean reconnectSignaled = false;

	@PostConstruct
	public void prepareSocketFactory() {
		try {
			logger.info("Initializing door proxy...");
			SSLContext sc = SSLContext.getInstance("TLSv1.2");
			SecureRandom rand = new SecureRandom();

			KeyStore ks = loadKeystore();
			TrustManager[] tm = getTrustManagers(ks);
			KeyManager[] km = getKeyManagers(ks);
			sc.init(km, tm, rand);
			ssf = sc.getSocketFactory();
		} catch (Throwable t) {
			throw new RuntimeException("Failed to init door proxy!", t);
		}
	}

	public void start() {
		logger.info("Starting DoorProxy Listener...");
		t = new Thread(this, "DoorProxy Listener");
		//t.setContextClassLoader(Thread.currentThread().getContextClassLoader());
		t.setDaemon(true);
		t.start();
	}

	@PreDestroy
	public void stop() throws InterruptedException, IOException {
		if (receiver != null) {
			receiver.stop();
		}
		if (t != null) {
			logger.info("Stopping DoorProxy Listener...");
			isRunning = false;
			if (this.socket != null) {
				this.socket.close();
			}
			t.interrupt();
			t.join(3000);
		}
	}

	public BufferedReader getSocketReader() throws UnsupportedEncodingException, IOException {
		return new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
	}

	@Override
	public void run() {
		try {
			while (isRunning) {
				logger.info("Connecting to " + host + ":" + port + "...");
				try (SSLSocket s = (SSLSocket) ssf.createSocket(host, port)) {
					logger.info("Connected!");
					initConnection(s);
					try (BufferedWriter out = new BufferedWriter(
							new OutputStreamWriter(s.getOutputStream(), "UTF-8"))) {
						this.socketOut = out;
						logger.info("Registering doors : " + secrets);
						String[] secretArray = secrets.split(",");
						RegisterCommand registerCmd = new RegisterCommand(secretArray);
						sendCommand(registerCmd);
						receiver = new DoorCommandReceiver(this);
						receiver.start();
						while (isRunning) {
							try {
								Thread.sleep(keepAliveInSecs * 1000);
							} catch (InterruptedException e) {
								if (reconnectSignaled) {
									// interrupted by receiver: signal me to
									// reconnect
									break;
								} else {
									// interrupted by service stop
									throw e;
								}
							}
							if (this.sendKeepAlive) {
								sendCommand(registerCmd);
							} else {
								logger.info("Ignoring sending keepalive while executing command.");
							}
						}
					}
				} catch (IOException e) {
					logger.warn("Connection error!", e);
					logger.info("Will reconnect in " + reconDelaySecs + " seconds.");
					Thread.sleep(reconDelaySecs * 1000);
				} finally {
					logger.info("Disconnected!");
				}

			}
		} catch (InterruptedException e) {
			logger.warn("Door Proxy is interrupted!");
		} catch (Throwable t) {
			logger.error("Door Proxy is stopped by unexpected error!", t);
		} finally {
			logger.info("Door Proxy stopped.");
		}
	}

	private void initConnection(SSLSocket s) {
		isRunning = true;
		reconnectSignaled = false;
		sendKeepAlive = true;
		this.socket = s;
	}

	private TrustManager[] getTrustManagers(KeyStore ks) {
		try {
			TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			tmf.init(ks);
			return tmf.getTrustManagers();
		} catch (Exception e) {
			throw new RuntimeException("Failed to load the trust managers for TLS connection.", e);
		}
	}

	private KeyManager[] getKeyManagers(KeyStore ks)
			throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException {
		KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		kmf.init(ks, ksPass.toCharArray());
		return kmf.getKeyManagers();
	}

	private KeyStore loadKeystore() throws IOException, KeyStoreException, NoSuchAlgorithmException,
			CertificateException, FileNotFoundException {
		File store = new File(keyStorePath);
		logger.info("Loading trust managers from keystore :" + store.getCanonicalPath());
		KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
		ks.load(new FileInputStream(store), ksPass.toCharArray());
		printAliases(ks);
		return ks;
	}

	private void printAliases(KeyStore ks) throws KeyStoreException {
		List<String> aliases = new ArrayList<String>(ks.size());
		Enumeration<String> e = ks.aliases();
		while (e.hasMoreElements()) {
			aliases.add(e.nextElement());
		}
		logger.info("Loaded keystore aliases : " + aliases);
	}

	public synchronized void sendCommand(Command cmd) throws RecoverableException, IOException {
		try {
			logger.info("Command sending: " + cmd);
			String sendMsg = cmd.serializeS();
			logger.info("Message sent : " + sendMsg);
			sendCommandMsg(this.socketOut, sendMsg);
		} catch (JsonProcessingException e) {
			String message = "Failed to serialize the response command. Reason : " + e.getMessage();
			logger.warn(message, e);
			throw new RecoverableException(message);
		}
	}

	private void sendCommandMsg(BufferedWriter out, String sendMsg) throws IOException {
		out.write(sendMsg);
		out.newLine();
		out.newLine();
		out.flush();
	}

	public void beginExec() {
		this.sendKeepAlive = false;
	}

	public void endExec() {
		this.sendKeepAlive = true;
	}

	public void signalReconnect() {
		this.reconnectSignaled = true;
		this.t.interrupt();
	}

}
