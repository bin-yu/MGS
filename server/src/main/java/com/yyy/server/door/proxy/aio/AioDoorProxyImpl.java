package com.yyy.server.door.proxy.aio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.InterruptedByTimeoutException;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.yyy.proxy.common.Command;
import com.yyy.proxy.common.client.DoorResponseCommand;
import com.yyy.proxy.common.client.RegisterCommand;
import com.yyy.proxy.common.server.DoorRequestCommand;
import com.yyy.server.door.proxy.DoorCommandException;
import com.yyy.server.door.proxy.DoorProxy;

public class AioDoorProxyImpl implements DoorProxy {
    private static Logger logger = LoggerFactory.getLogger(AioReadHandler.class);
    Map<String, LinkedBlockingQueue<DoorResponseCommand>> responseMap = new ConcurrentHashMap<String, LinkedBlockingQueue<DoorResponseCommand>>();
    private int readTimeout = 10;
    private int writeTimeout = 10;
    private AsynchronousSocketChannel sc;
    private AioDoorProxyFacade proxyFacade;
    private String remoteAddr;

    private String[] secrets;

    public AioDoorProxyImpl(AioDoorProxyFacade doorProxyFacade, AsynchronousSocketChannel sc) {
        this.proxyFacade = doorProxyFacade;
        this.sc = sc;
        try {
            this.remoteAddr = sc.getRemoteAddress().toString();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public void setWriteTimeout(int writeTimeout) {
        this.writeTimeout = writeTimeout;
    }

    @Override
    public DoorResponseCommand sendCommand(DoorRequestCommand cmd) throws DoorCommandException {
        // clear old response
        prepareResponseQueueFor(cmd);
        _sendCmd(cmd);
        // check the response received by listener
        return waitForResponse(cmd);
    }

    private DoorResponseCommand waitForResponse(DoorRequestCommand cmd) throws DoorCommandException {
        LinkedBlockingQueue<DoorResponseCommand> queue = getResponseQueueFor(cmd);
        try {
            return queue.poll(readTimeout, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            String msg = "Sending command to door proxy was interrupted. reason:" + e.getMessage();
            logWarn(msg, e);
            this.stop();
            throw new DoorCommandException(msg);
        }
    }

    private LinkedBlockingQueue<DoorResponseCommand> getResponseQueueFor(Command cmd) {
        LinkedBlockingQueue<DoorResponseCommand> queue = responseMap.get(cmd.getSecret());
        if (queue == null) {
            queue = new LinkedBlockingQueue<DoorResponseCommand>(1);
            responseMap.put(cmd.getSecret(), queue);
        }
        return queue;
    }

    private void prepareResponseQueueFor(DoorRequestCommand cmd) {
        LinkedBlockingQueue<DoorResponseCommand> queue = getResponseQueueFor(cmd);
        queue.clear();
    }

    private void storeResponse(DoorResponseCommand cmd) {
        LinkedBlockingQueue<DoorResponseCommand> queue = getResponseQueueFor(cmd);
        try {
            queue.offer(cmd, writeTimeout, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logWarn("Dropping response because queue is full : " + cmd, e);
        }
    }

    private synchronized void _sendCmd(Command cmd) throws DoorCommandException {
        logInfo("Sending command: " + cmd);
        String sendMsg;
        try {
            sendMsg = cmd.serializeS();
            sendMsg += "\r\n\r\n";
            ByteBuffer buf = ByteBuffer.wrap(sendMsg.getBytes("UTF-8"));
            sc.write(buf, writeTimeout, TimeUnit.SECONDS, buf, new AioSendHandler());
            logInfo("Message sent:" + sendMsg);
        } catch (JsonProcessingException e) {
            throw new DoorCommandException("Failed to serialize the command : " + e.getMessage());
        } catch (IOException e) {
            logWarn("Error occurs while sending message : " + e.getMessage(), e);
            this.stop();
            throw new DoorCommandException("Failed to send command to door proxy. reason:" + e.getMessage());
        }
    }

    @Override
    public boolean isClosed() {
        // TODO Auto-generated method stub
        return false;
    }

    public void start() {
        try {
            ByteBuffer clientBuffer = ByteBuffer.allocate(ProxyRequest.MAX_REQ_SIZE);
            while (true) {
                Future<Integer> future = sc.read(clientBuffer);
                int cnt = future.get(10, TimeUnit.SECONDS);
                if (cnt == -1) {
                    throw new IOException("Socket was closed before receiving register command!");
                }
                if (ProxyRequest.isRequestComplete(clientBuffer)) {
                    String msg = ProxyRequest.parseRequest(clientBuffer);
                    Command cmd = Command.deserializeS(msg);
                    if (cmd instanceof RegisterCommand) {
                        handleRegister((RegisterCommand) cmd);
                        startListen();
                        break;
                    } else {
                        throw new IOException("The first command must be register command!. Unexpected command received:" + cmd);
                    }
                }
            }
        } catch (Exception e) {
            logWarn("failed to start the proxy connection", e);
            this.stop();
        }
    }

    private void startListen() {
        ByteBuffer clientBuffer = ByteBuffer.allocate(ProxyRequest.MAX_REQ_SIZE);
        // 读数据到clientBuffer, 同时将clientBuffer作为attachment
        sc.read(clientBuffer, clientBuffer, new AioReadHandler());
    }

    protected void receivedCommand(Command cmd) {
        if (cmd instanceof RegisterCommand) {
            handleRegister((RegisterCommand) cmd);
        } else if (cmd instanceof DoorResponseCommand) {
            storeResponse((DoorResponseCommand) cmd);
        }
    }

    private void handleRegister(RegisterCommand cmd) {
        if (secrets == null) {
            secrets = cmd.getSecrets();
            logInfo("Registering for secrets : " + Arrays.toString(secrets));
            proxyFacade.registerDoors(this, secrets);
        }
    }

    void stop() {
        logInfo("Stopping proxy connection...");
        if (secrets != null) {
            proxyFacade.unRegisterDoors(this, secrets);
        }
        try {
            sc.close();
        } catch (IOException e) {
            logWarn(e.getMessage(), e);
        }
    }

    private void logInfo(String msg) {
        logger.info("[" + this.remoteAddr + "] " + msg);
    }

    private void logWarn(String msg, Throwable e) {
        logger.warn("[" + this.remoteAddr + "] " + msg, e);
    }

    private void logWarn(String msg) {
        logger.warn("[" + this.remoteAddr + "] " + msg);
    }

    class AioReadHandler implements CompletionHandler<Integer, ByteBuffer> {
        @Override
        public void completed(Integer i, ByteBuffer buf) {
            // 读到的字节数 > 0 
            if (i >= 0) {
                if (ProxyRequest.isRequestComplete(buf)) {
                    try {
                        String msg = ProxyRequest.parseRequest(buf);
                        logInfo("Message received : " + msg);
                        Command resp = Command.deserializeS(msg);
                        logInfo("Command received : " + resp);
                        receivedCommand(resp);
                    } catch (IOException e) {
                        // ignore
                        logWarn("Error occurs while parsing and handling the command: " + e.getMessage(), e);
                    }
                }
                // 关键:  尽快发起下一次异步读
                sc.read(buf, buf, this);

            } else if (i == -1) {
                // 读失败, 就是客户端断开了
                logWarn("Socket read has reached the end of the stream. The peer socket SHOULD have been closed.");
                stop();
            }
        }

        @Override
        public void failed(Throwable t, ByteBuffer attachment) {
            if (!(t instanceof InterruptedByTimeoutException)) {
                logWarn("IO error while reading the proxy connection!", t);
                stop();
            }
        }
    }
    class AioSendHandler implements CompletionHandler<Integer, ByteBuffer> {


        @Override
        public void completed(Integer i, ByteBuffer buf) {
            if (i > 0) {
                sc.write(buf, buf, this);
            } else if (i == -1) {
                logWarn("Socket read has reached the end of the stream. The peer socket SHOULD have been closed.");
                stop();
            }

        }

        @Override
        public void failed(Throwable t, ByteBuffer attachment) {
            if (!(t instanceof InterruptedByTimeoutException)) {
                logWarn("IO error while writing to the proxy connection!", t);
                stop();
            }
        }

    }
}
