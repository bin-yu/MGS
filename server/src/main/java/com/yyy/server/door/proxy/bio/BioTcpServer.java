package com.yyy.server.door.proxy.bio;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
@Lazy
public class BioTcpServer {
	private static final String KEYSTORE = "/keystore.jks";
	private Logger logger = LoggerFactory.getLogger(BioTcpServer.class);
	@Autowired
	private BioDoorProxyFacade doorProxyFac;
	@Value("${mgs.door.proxy-server.accept-thread-cnt}")
	private int acceptThreadCnt;
	@Value("${mgs.door.proxy-server.port}")
	private int port;
	@Value("${mgs.door.proxy-server.keystore.password}")
	private String ksPass;
	private Thread acceptThread;
	private volatile boolean isRunning = true;

	public void start() throws Exception {
		SSLContext sc = initSSLContext();
		SSLServerSocketFactory ssf = sc.getServerSocketFactory();
		logger.info("Listening on port : "+port);
		SSLServerSocket serverSocket = (SSLServerSocket) ssf.createServerSocket(port);
		serverSocket.setNeedClientAuth(true);
		serverSocket.setEnabledProtocols(new String[]{"TLSv1.2"});
		acceptThread = new Thread(new BIOAcceptRunnable(serverSocket),"Proxy Acceptor Thread");
		acceptThread.setDaemon(true);
		acceptThread.start();
	}
    private SSLContext initSSLContext() throws NoSuchAlgorithmException, KeyStoreException, IOException, CertificateException, UnrecoverableKeyException, Exception,
                                    KeyManagementException {
        SSLContext sc = SSLContext.getInstance("TLSv1.2");
		SecureRandom rand = new SecureRandom();
		KeyStore ks = loadKeystore();
		KeyManager[] km = getKeyManagers(ks);
		TrustManager[] tm = getTrustManagers(ks);
		sc.init(km , tm , rand );
        return sc;
    }
	private KeyManager[] getKeyManagers(KeyStore ks) throws NoSuchAlgorithmException, UnrecoverableKeyException, KeyStoreException, CertificateException, IOException {
		KeyManagerFactory kmf=KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		kmf.init(ks, ksPass.toCharArray());
		return kmf.getKeyManagers();
	}
	private KeyStore loadKeystore()
			throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
		logger.info("Loading keystore :" + KEYSTORE);
		KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
		ks.load(this.getClass().getResourceAsStream(KEYSTORE), ksPass.toCharArray());
		return ks;
	}
	private TrustManager[] getTrustManagers(KeyStore ks) throws Exception {
		TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		tmf.init(ks);
		return tmf.getTrustManagers();
	}

	public void stop() throws InterruptedException{
		isRunning = false;
		acceptThread.interrupt();
		acceptThread.join(2000);
	}
	

	private void closeSocket(Socket s) {
		if (s != null) {
			try {
				s.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public class BIOAcceptRunnable implements Runnable{
		private ServerSocket serverSock;
		private ExecutorService proxyConnectionRegPool;
		
		public BIOAcceptRunnable(ServerSocket serverSock) {
			super();
			this.serverSock = serverSock;
			proxyConnectionRegPool = Executors.newFixedThreadPool(acceptThreadCnt, new ThreadFactory() {

				@Override
				public Thread newThread(Runnable r) {
					Thread t = new Thread(r, "Proxy Connection Register");
					t.setDaemon(true);
					return t;
				}

			});
		}

		@Override
		public void run() {
			while(isRunning && !serverSock.isClosed()){
				final Socket sc;
				try {
					sc=serverSock.accept();
					proxyConnectionRegPool.execute(()->{
						try {
							doorProxyFac.registerProxySocket(sc);
						} catch (Throwable t) {
							logger.warn("Failed to register proxy connection!", t);
							closeSocket(sc);
						}
					});
				} catch (IOException e) {
					logger.warn("Exception happened while accepting connection!",e);
				}
			}
			try {
				serverSock.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	/*public class AcceptorRunnable implements Runnable {

		private Selector acceptSelector;
		private ExecutorService proxyConnectionRegPool;

		public AcceptorRunnable(Selector acceptSelector2, int acceptThreadCnt) {
			this.acceptSelector = acceptSelector2;
			proxyConnectionRegPool = Executors.newFixedThreadPool(acceptThreadCnt, new ThreadFactory() {

				@Override
				public Thread newThread(Runnable r) {

					return new Thread(r, "Proxy Connection Register");
				}

			});
		}

		@Override
		public void run() {
			int keysAdded = 0;

			// Here's where everything happens. The select method will
			// return when any operations registered above have occurred, the
			// thread has been interrupted, etc.
			try {
				while ((keysAdded = acceptSelector.select()) > 0) {
					// Someone is ready for I/O, get the ready keys
					Set<SelectionKey> readyKeys = acceptSelector.selectedKeys();
					Iterator<SelectionKey> i = readyKeys.iterator();

					// Walk through the ready keys collection and process date
					// requests.
					while (i.hasNext()) {
						SelectionKey sk = i.next();
						i.remove();
						if (sk.isValid() && sk.isAcceptable()) {
							// The key indexes into the selector so you
							// can retrieve the socket that's ready for I/O
							ServerSocketChannel nextReady = (ServerSocketChannel) sk.channel();
							try {
								SocketChannel socketChannel = nextReady.accept();
								if (socketChannel == null) {
									logger.info("No Connection available!");
									continue;
								}
								proxyConnectionRegPool.execute(() -> {
									// Accept the request
									Socket s = null;
									try {
										s = socketChannel.socket();
										registerSocket(s);
									} catch (Throwable t) {
										logger.warn("Failed to register proxy connection!", t);
										closeSocket(s);
									}
								});
							} catch (IOException e) {
								logger.warn("Failed to accept the proxy connection", e);
							}

						}

					}
				}
			} catch (Throwable t) {
				logger.warn("Unexpected error occured in proxy acceptor thread!", t);
			}
		}

		

	}*/
	

}
