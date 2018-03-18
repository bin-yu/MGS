package org.yyy.doorproxy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yyy.proxy.common.Command;
import com.yyy.proxy.common.client.ErrorResponseCommand;
import com.yyy.proxy.common.server.DoorRequestCommand;

public class DoorCommandReceiver implements Runnable {
	private static Logger logger = LoggerFactory.getLogger(DoorCommandReceiver.class);
	DoorProxy proxy;
	private Function<String, CommandExecutor> executorFac;
	private Thread t;
	private boolean isRunning = true;

	public DoorCommandReceiver(DoorProxy proxy) {
		super();
		this.proxy = proxy;
		this.executorFac = proxy.executorFac;
	}
	public void start() {
		logger.info("Starting DoorProxy Receiver...");
		t = new Thread(this, "DoorProxy Receiver");
		t.setDaemon(true);
		t.start();
	}
	public void stop() throws InterruptedException, IOException {
		if (t != null) {
			logger.info("Stopping DoorProxy Receiver...");
			isRunning  = false;
			t.interrupt();
			t.join(500);
		}
	}
	@Override
	public void run() {
		try (BufferedReader in = proxy.getSocketReader()) {
			while (isRunning) {
				DoorRequestCommand cmd = null;
				try {
					cmd = receiveCommand(in);
					proxy.beginExec();
					Command respCmd = executorFac.apply(cmd.getProtocol()).execute(cmd);
					proxy.sendCommand(respCmd);
				} catch (RecoverableException e) {
					// send error response
					logger.info("Sending Error Response : " + e.getMessage());
					String secret = "";
					if (cmd != null) {
						secret = cmd.getSecret();
					}
					ErrorResponseCommand respCmd = new ErrorResponseCommand(secret, e.getMessage());
					proxy.sendCommand(respCmd);
				} finally{
					proxy.endExec();
				}
			}
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			logger.info("DoorProxy Receiver encounters IO Error!",e);
		} finally{
			logger.info("DoorProxy Receiver stopped.");
			proxy.signalReconnect();
		}
	}

	private DoorRequestCommand receiveCommand(BufferedReader in) throws RecoverableException, IOException {
		String msg = in.readLine();
		logger.info("Message received : " + msg);
		if (msg == null) {
			logger.info("Received null message, the connection may be disconnected!");
			throw new IOException("Connection is disconnected!");
		}
		try {
			Command cmd = Command.deserializeS(msg);
			logger.info("Command received : " + cmd);
			if (!(cmd instanceof DoorRequestCommand)) {
				throw new RecoverableException("Unexpected command type:" + cmd.getClass().getSimpleName());
			}
			return (DoorRequestCommand) cmd;
		} catch (IOException e) {
			String message = "Failed to deserialize the request command. Reason : " + e.getMessage();
			logger.warn(message, e);
			throw new RecoverableException(message);
		}
	}

}
