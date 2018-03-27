package com.yyy.server.door.proxy.aio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

import javax.net.ssl.SSLContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yyy.server.door.proxy.bio.BioDoorProxyFacade;

//这里的参数受实际调用它的函数决定。本例是服务端socket.accetp调用决定
public class AioAcceptHandler implements CompletionHandler<AsynchronousSocketChannel, AsynchronousServerSocketChannel> {

    private static Logger logger = LoggerFactory.getLogger(AioAcceptHandler.class);
	private AioTcpServer aioTcpServer;

    public AioAcceptHandler(AioTcpServer aioTcpServer) {
        this.aioTcpServer= aioTcpServer;
    }

    @Override
    public void completed(AsynchronousSocketChannel socket, AsynchronousServerSocketChannel attachment) { //注意第一个是客户端socket，第二个是服户端socket
        String remoteAddr = "Unknown";
        try {
            attachment.accept(attachment, this); // attachment就是Listening Socket
            remoteAddr = socket.getRemoteAddress().toString();
            logger.info("New proxy connection comes from : " + remoteAddr);
            aioTcpServer.startNewConnection(socket);
        } catch (Throwable t) {
            logger.warn("Failed to accept socket connection from " + remoteAddr, t);
            try {
				socket.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        }
    }



    @Override
    public void failed(Throwable t, AsynchronousServerSocketChannel attachment) {
        logger.warn("Failed to accept connections!",t);
    }

    

}

