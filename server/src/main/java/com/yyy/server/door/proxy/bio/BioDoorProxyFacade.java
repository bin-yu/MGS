package com.yyy.server.door.proxy.bio;

import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.yyy.proxy.common.client.DoorResponseCommand;
import com.yyy.proxy.common.server.DoorRequestCommand;
import com.yyy.server.door.proxy.DoorCommandException;
import com.yyy.server.door.proxy.DoorProxy;
import com.yyy.server.door.proxy.DoorProxyFacade;

public class BioDoorProxyFacade implements DoorProxyFacade {
	private static Logger logger = LoggerFactory.getLogger(BioDoorProxyFacade.class);
	private static Map<String, DoorProxy> doorProxyMap = new ConcurrentHashMap<String, DoorProxy>();
	@Value("${mgs.door.proxy-server.read-timeout-secs}")
	private int readTimeout;
	@Value("${mgs.door.proxy-server.listener-thread-cnt}")
	private int listenerThreadCnt;
	@Value("${mgs.door.proxy-server.keep-alive-secs}")
	private int keepAliveInterval;
	@Autowired
	private BioTcpServer bioServer;
	private ScheduledExecutorService listenerPool;

	@PostConstruct
	public void init() throws Exception {
		listenerPool = Executors.newScheduledThreadPool(listenerThreadCnt, new ThreadFactory() {

			@Override
			public Thread newThread(Runnable r) {
				Thread t = new Thread(r, "DoorProxy listener");
				t.setDaemon(true);
				return t;
			}

		});
		bioServer.start();
	}

	@PreDestroy
	public void shutdown() {
		try {
			bioServer.stop();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		listenerPool.shutdownNow();
		try {
			listenerPool.awaitTermination(3, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	ScheduledFuture<?> scheduleListener(Runnable listener) {
		return listenerPool.scheduleWithFixedDelay(listener, 0, keepAliveInterval, TimeUnit.SECONDS);
	}

	public void registerProxySocket(Socket s) throws IOException {
		new BioDoorProxyImpl(this, s, readTimeout * 1000).start();
	}

	public void registerDoors(DoorProxy proxy, String[] secrets) {
		for (String secret : secrets) {
			DoorProxy oldProxy = doorProxyMap.put(secret, proxy);
			if (oldProxy != null) {
				logger.info("Replacing old proxy connection for door secret :" + secret + "...");
			}
		}
	}

	void unRegisterDoors(DoorProxy proxy, String[] secrets) {
		for (String secret : secrets) {
			if (proxy == doorProxyMap.get(secret)) {
				DoorProxy oldProxy = doorProxyMap.remove(secret);
				if (oldProxy != null) {
					logger.info("Unregister old proxy connection for door secret :" + secret + "...");
				}
			}
		}
	}

	public DoorResponseCommand sendCommand(String doorSecret, DoorRequestCommand cmd) throws DoorCommandException {

		DoorProxy proxy = getDoorConnection(doorSecret);
		return proxy.sendCommand(cmd);

	}

	public DoorProxy getDoorConnection(String doorSecret) throws DoorCommandException {
		DoorProxy proxy = doorProxyMap.get(doorSecret);

		if (proxy == null) {
			throw new DoorCommandException("Door proxy not connected! secret = " + doorSecret);
		}
		if (proxy.isClosed()) {
			throw new DoorCommandException("Door proxy connection has been closed! secret = " + doorSecret);
		}
		return proxy;
	}

}
