package com.yyy.server.door.proxy.aio;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.yyy.proxy.common.client.DoorResponseCommand;
import com.yyy.proxy.common.server.DoorRequestCommand;
import com.yyy.server.door.proxy.DoorCommandException;
import com.yyy.server.door.proxy.DoorProxy;

@Service
public class AioDoorProxyFacade implements AioConnectionHandler{
	private static Logger logger = LoggerFactory.getLogger(AioDoorProxyFacade.class);
	private static Map<String, DoorProxy> doorProxyMap = new ConcurrentHashMap<String, DoorProxy>();
	
	@Override
	public void startNewConnection(AsynchronousSocketChannel sc) {
		new AioDoorProxyImpl(this,sc).start();
		
		
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
