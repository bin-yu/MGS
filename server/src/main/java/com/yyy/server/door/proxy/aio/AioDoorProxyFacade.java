package com.yyy.server.door.proxy.aio;

import java.nio.channels.AsynchronousSocketChannel;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.yyy.server.door.proxy.DoorCommandException;
import com.yyy.server.door.proxy.DoorProxy;
import com.yyy.server.door.proxy.DoorProxyFacade;


public class AioDoorProxyFacade implements DoorProxyFacade, AioConnectionHandler {
	private static Logger logger = LoggerFactory.getLogger(AioDoorProxyFacade.class);
	private static Map<String, DoorProxy> doorProxyMap = new ConcurrentHashMap<String, DoorProxy>();
    @Value("${mgs.door.proxy-server.read-timeout-secs}")
    private int readTimeout;
    @Value("${mgs.door.proxy-server.write-timeout-secs}")
    private int writeTimeout;
    @Autowired
    private AioTcpServer aioServer;
    @PostConstruct
    public void start() throws Exception{
    	aioServer.start();
    }
    @PreDestroy
    public void stop(){
    	aioServer.stop();
    }
	@Override
    public void handleNewConnection(AsynchronousSocketChannel sc) {
        AioDoorProxyImpl doorProxy = new AioDoorProxyImpl(this, sc);
        doorProxy.setReadTimeout(readTimeout);
        doorProxy.setWriteTimeout(writeTimeout);
        doorProxy.start();
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
