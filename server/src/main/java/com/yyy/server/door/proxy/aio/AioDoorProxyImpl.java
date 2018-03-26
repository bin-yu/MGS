package com.yyy.server.door.proxy.aio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.yyy.proxy.common.Command;
import com.yyy.proxy.common.client.DoorResponseCommand;
import com.yyy.proxy.common.client.RegisterCommand;
import com.yyy.proxy.common.server.DoorRequestCommand;
import com.yyy.server.door.proxy.DoorCommandException;
import com.yyy.server.door.proxy.DoorProxy;

public class AioDoorProxyImpl implements DoorProxy {
	private static Logger logger = LoggerFactory.getLogger(AioReadHandler.class);
	Map<String, LinkedBlockingQueue<DoorResponseCommand>> responseMap = new ConcurrentHashMap<String, LinkedBlockingQueue<DoorResponseCommand>>();

	private AsynchronousSocketChannel sc;
	private AioDoorProxyFacade proxyFacade;
	private Object remoteAddr;

	private String[] secrets;

	public AioDoorProxyImpl(AioDoorProxyFacade doorProxyFacade, AsynchronousSocketChannel sc) {
		this.proxyFacade = doorProxyFacade;
		this.sc = sc;
		try {
			this.remoteAddr = sc.getRemoteAddress().toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public DoorResponseCommand sendCommand(DoorRequestCommand cmd) throws DoorCommandException {
		// clear old response
		prepareResponseQueueFor(cmd);
		_sendCmd(cmd);
		// check the response received by listener
		return waitForResponse(cmd);
	}

	private DoorResponseCommand waitForResponse(DoorRequestCommand cmd) throws DoorCommandException {
		LinkedBlockingQueue<DoorResponseCommand> queue = getResponseQueueFor(cmd);
		try {
			return queue.poll(10, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			logWarn("Error occurs : " + e.getMessage(), e);
			this.stop();
			throw new DoorCommandException("Sending command to door proxy was interrupted. reason:" + e.getMessage());
		}
	}

	private LinkedBlockingQueue<DoorResponseCommand> getResponseQueueFor(Command cmd) {
		LinkedBlockingQueue<DoorResponseCommand> queue = responseMap.get(cmd.getSecret());
		if (queue == null) {
			queue = new LinkedBlockingQueue<DoorResponseCommand>(1);
			responseMap.put(cmd.getSecret(), queue);
		}
		return queue;
	}

	private void prepareResponseQueueFor(DoorRequestCommand cmd) {
		LinkedBlockingQueue<DoorResponseCommand> queue = getResponseQueueFor(cmd);
		queue.clear();
	}

	private void storeResponse(DoorResponseCommand cmd) {
		LinkedBlockingQueue<DoorResponseCommand> queue = getResponseQueueFor(cmd);
		try {
			queue.offer(cmd, 10, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			logWarn("Dropping response because queue is full : " + cmd, e);
		}
	}

	private synchronized void _sendCmd(Command cmd) throws DoorCommandException {
		logInfo("Sending command: " + cmd);
		String sendMsg;
		try {
			sendMsg = cmd.serializeS();
			sendMsg += "\r\n\r\n";
			sc.write(ByteBuffer.wrap(sendMsg.getBytes("UTF-8"))).get();
			logInfo("Message sent:" + sendMsg);
		} catch (JsonProcessingException e) {
			throw new DoorCommandException("Failed to serialize the command : " + e.getMessage());
		} catch (IOException e) {
			logWarn("Error occurs : " + e.getMessage(), e);
			this.stop();
			throw new DoorCommandException("Failed to send command to door proxy. reason:" + e.getMessage());
		} catch (InterruptedException | ExecutionException e) {
			logWarn("Error occurs : " + e.getMessage(), e);
			this.stop();
			throw new DoorCommandException("Sending command to door proxy was interrupted. reason:" + e.getMessage());
		}
	}

	@Override
	public boolean isClosed() {
		// TODO Auto-generated method stub
		return false;
	}

	public void start() {
		ByteBuffer clientBuffer = ByteBuffer.allocate(1024);

		// 读数据到clientBuffer, 同时将clientBuffer作为attachment
		sc.read(clientBuffer, clientBuffer, new AioReadHandler(sc) {

			@Override
			protected void handleRequest(String msg) {
				Command resp = null;
				try {
					resp = Command.deserializeS(msg);
					logInfo("Command received : " + resp);
					receivedCommand(resp);
				} catch (IOException e) {
					// ignore
					logWarn("Error occurs : " + e.getMessage(), e);
				}
			}

		});
	}

	protected void receivedCommand(Command cmd) {
		if (cmd instanceof RegisterCommand) {
			if (secrets == null) {
				secrets = ((RegisterCommand) cmd).getSecrets();
				logInfo("Registering for secrets : " + Arrays.toString(secrets));
				proxyFacade.registerDoors(this, secrets);
			}
		} else if (cmd instanceof DoorResponseCommand) {
			storeResponse((DoorResponseCommand) cmd);
		}
	}

	void stop() {
		logInfo("Stopping proxy connection...");
		if (secrets != null) {
			proxyFacade.unRegisterDoors(this, secrets);
		}
		try {
			sc.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void logInfo(String msg) {
		logger.info("[" + this.remoteAddr + "] " + msg);
	}

	private void logWarn(String msg, Throwable e) {
		logger.warn("[" + this.remoteAddr + "] " + msg, e);
	}
}
