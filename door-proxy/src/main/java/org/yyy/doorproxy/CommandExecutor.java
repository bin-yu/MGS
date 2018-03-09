package org.yyy.doorproxy;

import com.yyy.proxy.common.client.DoorResponseCommand;
import com.yyy.proxy.common.server.DoorRequestCommand;

public interface CommandExecutor {
	DoorResponseCommand execute(DoorRequestCommand request);
}
