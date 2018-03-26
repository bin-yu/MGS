package com.yyy.server.door.proxy.aio;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

import javax.net.ssl.SSLContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//这里的参数受实际调用它的函数决定。本例是服务端socket.accetp调用决定
public class AioAcceptHandler implements CompletionHandler<AsynchronousSocketChannel, AsynchronousServerSocketChannel> {

    private static Logger logger = LoggerFactory.getLogger(AioAcceptHandler.class);
    private SSLContext sslContext;

    public AioAcceptHandler(SSLContext initSSLContext) {
        this.sslContext = initSSLContext;
    }

    @Override
    public void completed(AsynchronousSocketChannel socket, AsynchronousServerSocketChannel attachment) { //注意第一个是客户端socket，第二个是服户端socket
        String remoteAddr = "Unknown";
        try {
            attachment.accept(attachment, this); // attachment就是Listening Socket
            remoteAddr = socket.getRemoteAddress().toString();
            logger.info("New proxy connection comes from : " + remoteAddr);
            AsynSSLSocketChannel sc = new AsynSSLSocketChannel(sslContext, socket);
            // 开始读客户端
            startRead(sc);
        } catch (Exception e) {
            logger.warn("Failed to accept socket connection from " + remoteAddr, e);
        }
    }



    @Override
    public void failed(Throwable exc, AsynchronousServerSocketChannel attachment) {
        exc.printStackTrace();
    }

    //不是CompletionHandler的方法
    public void startRead(AsynSSLSocketChannel sc) {
        ByteBuffer clientBuffer = ByteBuffer.allocate(1024);
        //read的原型是
        //read(ByteBuffer dst, A attachment, CompletionHandler<Integer,? super A> handler) 
        //即它的操作处理器，的A型，是实际调用read的第二个参数，即clientBuffer。
        // V型是存有read的连接情况的参数
        AioReadHandler rd = new AioReadHandler(sc);
        // 读数据到clientBuffer, 同时将clientBuffer作为attachment
        sc.read(clientBuffer, clientBuffer, rd);
    }


}

