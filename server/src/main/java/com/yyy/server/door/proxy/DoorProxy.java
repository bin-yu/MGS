package com.yyy.server.door.proxy;

import com.yyy.proxy.common.client.DoorResponseCommand;
import com.yyy.proxy.common.server.DoorRequestCommand;

public interface DoorProxy {

	DoorResponseCommand sendCommand(DoorRequestCommand cmd) throws DoorCommandException;

	boolean isClosed();

}
