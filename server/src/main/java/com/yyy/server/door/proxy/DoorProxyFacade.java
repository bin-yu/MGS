package com.yyy.server.door.proxy;

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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.yyy.proxy.common.client.DoorResponseCommand;
import com.yyy.proxy.common.server.DoorRequestCommand;

@Service
public class DoorProxyFacade {
	private static Logger logger = LoggerFactory.getLogger(DoorProxyFacade.class);
	private static Map<String, DoorProxyStub> doorProxyMap = new ConcurrentHashMap<String, DoorProxyStub>();
	@Value("${mgs.door.proxy-server.read-timeout-secs}")
	private int readTimeout;
	@Value("${mgs.door.proxy-server.listener-thread-cnt}")
	private int listenerThreadCnt;
	@Value("${mgs.door.proxy-server.keep-alive-secs}")
	private int keepAliveInterval;

	private ScheduledExecutorService listenerPool;

	@PostConstruct
	public void init() {
		listenerPool = Executors.newScheduledThreadPool(listenerThreadCnt, new ThreadFactory() {

			@Override
			public Thread newThread(Runnable r) {
				Thread t = new Thread(r, "DoorProxy listener");
				t.setDaemon(true);
				return t;
			}

		});
	}

	@PreDestroy
	public void shutdown() {
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
		new DoorProxyStub(this, s, readTimeout * 1000).start();
	}

	void registerDoors(DoorProxyStub proxy, String[] secrets) {
		for (String secret : secrets) {
			DoorProxyStub oldProxy = doorProxyMap.put(secret, proxy);
			if (oldProxy != null) {
				logger.info("Replacing old proxy connection for door secret :" + secret + "...");
			}
		}
	}

	void unRegisterDoors(DoorProxyStub proxy, String[] secrets) {
		for (String secret : secrets) {
			if (proxy == doorProxyMap.get(secret)) {
				DoorProxyStub oldProxy = doorProxyMap.remove(secret);
				if (oldProxy != null) {
					logger.info("Unregister old proxy connection for door secret :" + secret + "...");
				}
			}
		}
	}

	public DoorResponseCommand sendCommand(String doorSecret, DoorRequestCommand cmd) throws DoorCommandException {

		DoorProxyStub proxy = getDoorConnection(doorSecret);
		return proxy.sendCommand(cmd);

	}

	public DoorProxyStub getDoorConnection(String doorSecret) throws DoorCommandException {
		DoorProxyStub proxy = doorProxyMap.get(doorSecret);

		if (proxy == null) {
			throw new DoorCommandException("Door proxy not connected! secret = " + doorSecret);
		}
		if (proxy.isClosed()) {
			throw new DoorCommandException("Door proxy connection has been closed! secret = " + doorSecret);
		}
		return proxy;
	}

}
