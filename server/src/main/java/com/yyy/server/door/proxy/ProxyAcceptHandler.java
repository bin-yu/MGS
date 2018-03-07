package com.yyy.server.door.proxy;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProxyAcceptHandler implements CompletionHandler<AsynchronousSocketChannel, AsynchronousServerSocketChannel> {

    private Logger logger = LoggerFactory.getLogger(ProxyAcceptHandler.class);
    private CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();
    @Override
    public void completed(AsynchronousSocketChannel socket, AsynchronousServerSocketChannel attachment) {
        try {
            logger.info("ProxyAcceptHandler.completed called");
            attachment.accept(attachment, this);
            logger.info("New client connection from : " + socket.getRemoteAddress().toString());
            sendRequest(socket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void failed(Throwable t, AsynchronousServerSocketChannel attachment) {
        logger.info("ProxyAcceptHandler exception happened!", t);
    }

    public void sendRequest(AsynchronousSocketChannel socket) throws Exception {
        String sendString = "Welcome " + socket.getRemoteAddress().toString();
        logInfo(socket, "Sending message : " + sendString);
        ByteBuffer buf = ByteBuffer.wrap(sendString.getBytes("UTF-8"));
        Future<Integer> future = socket.write(buf);
        logInfo(socket, "Bytes sent : " + future.get());
        receiveResp(socket);
    }

    private void receiveResp(AsynchronousSocketChannel socket) throws InterruptedException, ExecutionException, CharacterCodingException, IOException {
        logInfo(socket, "Reading response...");
        ByteBuffer dst = ByteBuffer.allocate(1024);
        Future<Integer> future = socket.read(dst);
        logInfo(socket, "Bytes received : " + future.get());
        dst.flip();
        String msg = decoder.decode(dst).toString();
        logInfo(socket, "Received message :" + msg);
    }

    private void logInfo(AsynchronousSocketChannel socket, String msg) {
        try {
            logger.info("[" + socket.getRemoteAddress() + "]" + msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
