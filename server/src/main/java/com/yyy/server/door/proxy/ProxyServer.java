package com.yyy.server.door.proxy;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
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
import org.springframework.stereotype.Service;

import com.yyy.proxy.common.Command;
import com.yyy.proxy.common.client.RegisterCommand;

@Service
public class ProxyServer {
	private static final String KEYSTORE = "/keystore.jks";
	private Logger logger = LoggerFactory.getLogger(ProxyServer.class);
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

	@PostConstruct
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
		
		/*
		
		// Selector for incoming time requests
		Selector acceptSelector = SelectorProvider.provider().openSelector();

		// Create a new server socket and set to non blocking mode
		ServerSocketChannel ssc = ServerSocketChannel.open();
		ssc.configureBlocking(false);
		

		// Bind the server socket to the local host and port
		InetAddress lh = InetAddress.getLocalHost();
		logger.info("Listening to " + lh.toString() + ":" + port + "...");
		InetSocketAddress isa = new InetSocketAddress(lh, port);
		ssc.socket().bind(isa);

		// Register accepts on the server socket with the selector. This
		// step tells the selector that the socket wants to be put on the
		// ready list when accept operations occur, so allowing multiplexed
		// non-blocking I/O to take place.
		SelectionKey acceptKey = ssc.register(acceptSelector, SelectionKey.OP_ACCEPT);

		pool = Executors.newFixedThreadPool(1, new ThreadFactory() {

			@Override
			public Thread newThread(Runnable r) {

				return new Thread(r, "Proxy Connection Acceptor");
			}

		});
		pool.execute(new AcceptorRunnable(acceptSelector, acceptThreadCnt));*/

		/*
		 * try { ExecutorService executor =
		 * Executors.newFixedThreadPool(threadCnt); AsynchronousChannelGroup
		 * asyncChannelGroup =
		 * AsynchronousChannelGroup.withThreadPool(executor); listener =
		 * AsynchronousServerSocketChannel.open(asyncChannelGroup).bind(new
		 * InetSocketAddress(port), 100); listener.accept(listener, new
		 * ProxyAcceptHandler()); } catch (IOException e) {
		 * logger.warn("Failed to start door proxy server!!!", e); }
		 */
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

	@PreDestroy
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

					return new Thread(r, "Proxy Connection Register");
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
