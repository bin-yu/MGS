package org.yyy.doorproxy;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yyy.proxy.common.client.DoorResponseCommand;
import com.yyy.proxy.common.client.ErrorResponseCommand;
import com.yyy.proxy.common.server.DoorRequestCommand;

public class TcpDoorCommandExecutor implements CommandExecutor {
	private static Logger logger = LoggerFactory.getLogger(TcpDoorCommandExecutor.class);

	@Override
	public DoorResponseCommand execute(DoorRequestCommand request) {
		DoorResponseCommand respCmd = null;
		try (TcpDoorConnector exec = new TcpDoorConnector(request.getDoorIp(), request.getDoorPort())) {
			exec.sendRequest(request.getBody());
			respCmd = new DoorResponseCommand(request.getSecret(), exec.receiveResponse());
		} catch (IOException e) {
			logger.warn("Failed to execute door command" + request, e);
			respCmd = new ErrorResponseCommand(request.getSecret(), e.getMessage());
		}
		return respCmd;
	}

}
