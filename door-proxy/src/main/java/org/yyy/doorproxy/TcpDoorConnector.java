package org.yyy.doorproxy;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import javax.xml.bind.DatatypeConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TcpDoorConnector implements Closeable {

	private static final int CONNECT_TIMEOUT = 5000;
	private static final int SO_TIMEOUT = 10000;
	private static int MAX_RESP_SIZE = 1024 * 1024;
	private static Logger logger = LoggerFactory.getLogger(TcpDoorConnector.class);
	private String ip;
	private int port;
	private Socket socket;
	private InputStream in;
	private OutputStream out;

	public TcpDoorConnector(String ip, int port) throws IOException {
		super();
		this.ip = ip;
		this.port = port;
		connect();
	}

	void connect() throws IOException {
		socket = new Socket();
		socket.connect(new InetSocketAddress(ip, port), CONNECT_TIMEOUT);
		socket.setSoTimeout(SO_TIMEOUT);
		in = socket.getInputStream();
		out = socket.getOutputStream();
		logger.info("[" + ip + "]DOOR CONNECTED!");
	}

	void disconnect() {
		try {
			if(socket!=null && socket.isClosed())socket.close();
		} catch (IOException e) {
			logger.warn("failed to disconnect to the door!" + ip, e);
		} finally {
			logger.info("[" + ip + "]DOOR DISCONNECTED!");
		}
	}

	public void sendRequest(byte[] request) throws IOException {
		logger.info("[" + ip + "]DOOR SEND DATA:" + DatatypeConverter.printHexBinary(request));
		out.write(request);
	}

	public byte[] receiveResponse() throws IOException {
		byte[] buffer = new byte[MAX_RESP_SIZE];
		int cnt = in.read(buffer);
		if (cnt > 0) {
			byte[] ret = new byte[cnt];
			System.arraycopy(buffer, 0, ret, 0, cnt);
			logger.info("[" + ip + "]DOOR RECV DATA:" + DatatypeConverter.printHexBinary(ret));
			return ret;
		} else {
			return new byte[0];
		}
	}

	@Override
	public void close() throws IOException {
		disconnect();
	}

}
