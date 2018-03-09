package org.yyy.doorproxy;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.annotation.PostConstruct;
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
public class DoorProxy {

	private static Logger logger = LoggerFactory.getLogger(DoorProxy.class);

	@Value("${proxy.reconnect.delayInSecs}")
	private int reconDelaySecs = 600 * 1000;
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
	
	@Autowired
	private TcpDoorCommandExecutor cmdExecutor;

	@PostConstruct
	public void prepareSocketFactory() {
		try {
			logger.info("Initializing door proxy...");
			SSLContext sc = SSLContext.getInstance("TLSv1.2");
			SecureRandom rand = new SecureRandom();

			KeyStore ks = loadKeystore();
			TrustManager[] tm = getTrustManagers(ks);
			KeyManager[] km = getKeyManagers(ks);
			sc.init(km , tm, rand);
			ssf = sc.getSocketFactory();
		} catch (Throwable t) {
			throw new RuntimeException("Failed to init door proxy!", t);
		}
	}

	

	public void start() throws InterruptedException, IOException {
		while (true) {
			logger.info("Connecting to " + host + ":" + port + "...");
			try (SSLSocket s = (SSLSocket) ssf.createSocket(host, port)) {
				logger.info("Connected!");
				try (BufferedWriter out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream(), "UTF-8"))) {
					logger.info("Registering doors : " + secrets);
					sendCommand(out, new RegisterCommand(secrets.split(",")));

					try (BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream(), "UTF-8"))) {
						while (true) {
							Command cmd = null;
							try {
								cmd = receiveCommand(in);
								logger.info("Command received : " + cmd);
								Command respCmd = cmdExecutor.execute((DoorRequestCommand) cmd);
								sendCommand(out, respCmd);
							} catch (RecoverableException e) {
								// send error response
								logger.info("Sending Error Response : " + e.getMessage());
								String secret = secrets;
								if (cmd != null) {
									secret = cmd.getSecret();
								}
								ErrorResponseCommand respCmd = new ErrorResponseCommand(secret, e.getMessage());
								sendCommand(out, respCmd);
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

		}
	}



	private Command receiveCommand(BufferedReader in) throws RecoverableException, IOException {
		String msg = in.readLine();
		logger.info("Message received : " + msg);
		if (msg == null) {
			logger.info("Received null message, the connection may be disconnected!");
			throw new IOException("Connection is disconnected!");
		}
		try {
			return Command.deserializeS(msg);
		} catch (IOException e) {
			String message = "Failed to deserialize the request command. Reason : " + e.getMessage();
			logger.warn(message, e);
			throw new RecoverableException(message);
		}
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
	private KeyManager[] getKeyManagers(KeyStore ks) throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException {
		KeyManagerFactory kmf=KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
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

	private void sendCommand(BufferedWriter out, Command cmd) throws RecoverableException, IOException {
		try {
			logger.info("Command sending: " + cmd);
			String sendMsg = cmd.serializeS();
			logger.info("Message sent : " + sendMsg);
			sendCommandMsg(out, sendMsg);
		} catch (JsonProcessingException e) {
			String message = "Failed to serialize the response command. Reason : " + e.getMessage();
			logger.warn(message, e);
			throw new RecoverableException(message);
		}
	}

	private void sendCommandMsg(BufferedWriter out, String sendMsg) throws IOException {
		out.write(sendMsg);
		out.newLine();
		out.flush();
	}

	private static class RecoverableException extends IOException {

		/**
		 * 
		 */
		private static final long serialVersionUID = 8755468527353626462L;

		public RecoverableException(String message) {
			super(message);
		}

	}
}
