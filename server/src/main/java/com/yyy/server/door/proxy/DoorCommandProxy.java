package com.yyy.server.door.proxy;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.yyy.proxy.common.Command;
import com.yyy.proxy.common.client.DoorResponseCommand;
import com.yyy.proxy.common.server.DoorRequestCommand;

@Service
public class DoorCommandProxy {
	private static Logger logger = LoggerFactory.getLogger(DoorCommandProxy.class);
	private static Map<String, Socket> doorSocketMap = new ConcurrentHashMap<String, Socket>();

	public void registerDoors(Socket s, String[] secrets) {
		for (String secret : secrets) {
			Socket oldS = doorSocketMap.put(secret, s);
			if (oldS != null) {
				logger.info("Closing old proxy connection for door secret :" + secret + "...");
				_closeSocket(oldS);
			}
		}
	}

	public DoorResponseCommand sendCommand(String doorSecret, DoorRequestCommand cmd) throws DoorCommandException {

		Socket s = getDoorConnection(doorSecret);
		try {
			_sendCmd(s, cmd);
			return (DoorResponseCommand) _receiveResp(s);
		} catch (IOException e) {
			logger.warn("IO exception while sending command, closing this connection!", e);
			closeSocket(doorSecret, s);
			throw new DoorCommandException(
					"Failed to send command to door proxy. secret = " + doorSecret + ",reason:" + e.getMessage());
		} catch (Throwable t) {
			logger.warn("unexpected exception while sending command!", t);
			throw new DoorCommandException(
					"Failed to send command to door proxy. secret = " + doorSecret + ",reason:" + t.getMessage());
		}

	}

	public Socket getDoorConnection(String doorSecret) throws DoorCommandException {
		Socket s = doorSocketMap.get(doorSecret);

		if (s == null) {
			throw new DoorCommandException("Door proxy not connected! secret = " + doorSecret);
		}
		if (s.isClosed()) {
			closeSocket(doorSecret, s);
			throw new DoorCommandException("Door proxy connection has been closed! secret = " + doorSecret);
		}
		return s;
	}

	private Command _receiveResp(Socket s) throws UnsupportedEncodingException, IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream(), "UTF-8"));
		String msg = in.readLine();
		logger.info("Message received : " + msg);
		Command resp = Command.deserializeS(msg);
		logger.info("Command received : " + resp);
		return resp;
	}

	private void _sendCmd(Socket s, Command cmd)
			throws UnsupportedEncodingException, IOException, JsonProcessingException {
		logger.info("Sending command: " + cmd);
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream(), "UTF-8"));
		String sendMsg = cmd.serializeS();
		out.write(sendMsg);
		out.newLine();
		out.flush();
		logger.info("Message sent:" + sendMsg);
	}

	private void closeSocket(String doorSecret, Socket s) {
		logger.info("Closing connection for door secret = " + doorSecret + "...");
		doorSocketMap.remove(doorSecret);
		_closeSocket(s);
	}

	private void _closeSocket(Socket s) {
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
