package com.yyy.server.door.proxy.bio;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.yyy.proxy.common.Command;
import com.yyy.proxy.common.client.DoorResponseCommand;
import com.yyy.proxy.common.client.RegisterCommand;
import com.yyy.proxy.common.server.DoorRequestCommand;
import com.yyy.server.door.proxy.DoorCommandException;
import com.yyy.server.door.proxy.DoorProxy;

public class BioDoorProxyImpl implements DoorProxy {

	private static Logger logger = LoggerFactory.getLogger(BioDoorProxyImpl.class);

	private Socket s;

	private String[] secrets = null;
	Map<String, DoorResponseCommand> responseMap = new ConcurrentHashMap<String, DoorResponseCommand>();

	private BufferedWriter out;

	private BufferedReader in;

	private ScheduledFuture<?> listenerFuture;

	private String remoteSocketAddress;

	private BioDoorProxyFacade proxyFacade;

	private boolean isClosed = false;

	private final int soTimeout;

	public BioDoorProxyImpl(BioDoorProxyFacade proxyFacade, Socket s, int soTimeout) {
		super();
		this.s = s;
		this.proxyFacade = proxyFacade;
		this.soTimeout = soTimeout;
	}

	public void start() throws IOException {
		remoteSocketAddress = s.getRemoteSocketAddress().toString();
		logInfo("New proxy connection comes in.");
		try {
			out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream(), "UTF-8"));
			startListen();
		} catch (IOException e) {
			logger.error("Socket encounters IO Error for " + secrets);
			this._closeSocket();
			throw e;
		}
	}

	public void stop() {
		logInfo("Stopping proxy connection...");
		if (secrets != null) {
			proxyFacade.unRegisterDoors(this, secrets);
		}
		isClosed = true;
		listenerFuture.cancel(true);
		this._closeSocket();
	}

	public DoorResponseCommand sendCommand(DoorRequestCommand cmd) throws DoorCommandException {
		// clear old response
		responseMap.remove(cmd.getSecret());
		_sendCmd(cmd);
		// check the response received by listener
		Command resp = responseMap.remove(cmd.getSecret());
		if (resp != null) {
			return (DoorResponseCommand) resp;
		}
		// directly read response from the socket
		return readDoorResponseCommand();

	}

	public boolean isClosed() {
		return this.isClosed;
	}

	private DoorResponseCommand readDoorResponseCommand() throws DoorCommandException {
		while (true) {
			Command resp = doReceive(soTimeout);
			if (resp == null) {
				throw new DoorCommandException("No response returned.");
			}
			if (resp instanceof DoorResponseCommand) {
				return (DoorResponseCommand) resp;
			} else {
				logInfo("Ignoring keepalive command!");
			}
		}
	}

	private void startListen() throws IOException {
		in = new BufferedReader(new InputStreamReader(s.getInputStream(), "UTF-8"));
		listenerFuture = proxyFacade.scheduleListener(() -> doReceive(1000));
		logInfo("Listener started.");
	}

	private synchronized Command doReceive(int timeout) {
		Command cmd;
		try {
			s.setSoTimeout(timeout);
			cmd = this._receiveResp();
			if (cmd instanceof RegisterCommand) {
				if (secrets == null) {
					secrets = ((RegisterCommand) cmd).getSecrets();
					logInfo("Registering for secrets : " + Arrays.toString(secrets));
					proxyFacade.registerDoors(this, secrets);
				}
			} else if (cmd instanceof DoorResponseCommand) {
				responseMap.put(cmd.getSecret(), (DoorResponseCommand) cmd);
			}
			return cmd;
		} catch (SocketTimeoutException e) {
			logInfo("Timeout reading message from socket : " + e.getMessage());
		} catch (Throwable e) {
			logger.warn("[" + this.remoteSocketAddress + "]Error occurs : " + e.getMessage(), e);
			this.stop();
		}
		return null;
	}

	private Command _receiveResp() throws IOException {
		logInfo("Receiving message...");
		String msg = null;
		while (true) {
			msg = in.readLine();
			if (msg == null) {
				//the end of the stream has been reached 
				return null;
			}
			msg = msg.trim();
			if (msg.length() > 0) {
				break;
			}
			//continue to read if any empty line was read.
		}
		logInfo("Message received : " + msg);
		Command resp = null;
		try {
			resp = Command.deserializeS(msg);
			logInfo("Command received : " + resp);
		} catch (IOException e) {
			// ignore
			logger.warn("[" + this.remoteSocketAddress + "]Error occurs : " + e.getMessage(), e);
		}
		return resp;
	}

	private synchronized void _sendCmd(Command cmd) throws DoorCommandException {
		logInfo("Sending command: " + cmd);
		String sendMsg;
		try {
			sendMsg = cmd.serializeS();

			out.write(sendMsg);
			out.newLine();
			out.flush();
			logInfo("Message sent:" + sendMsg);
		} catch (JsonProcessingException e) {
			throw new DoorCommandException("Failed to serialize the command : " + e.getMessage());
		} catch (IOException e) {
			logger.warn("[" + this.remoteSocketAddress + "]Error occurs : " + e.getMessage(), e);
			this.stop();
			throw new DoorCommandException("Failed to send command to door proxy. reason:" + e.getMessage());
		}
	}

	private void logInfo(String msg) {
		logger.info("[" + this.remoteSocketAddress + "]" + msg);
	}

	private void _closeSocket() {
		if (s != null && !s.isClosed()) {
			try {
				s.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
