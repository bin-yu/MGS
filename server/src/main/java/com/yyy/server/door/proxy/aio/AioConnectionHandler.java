package com.yyy.server.door.proxy.aio;

import java.nio.channels.AsynchronousSocketChannel;

public interface AioConnectionHandler {

	void handleNewConnection(AsynchronousSocketChannel sc);
	
}
