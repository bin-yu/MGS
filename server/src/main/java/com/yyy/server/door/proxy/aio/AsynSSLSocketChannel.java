package com.yyy.server.door.proxy.aio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLEngineResult.HandshakeStatus;
import javax.net.ssl.SSLSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AsynSSLSocketChannel {
    private static Logger logger = LoggerFactory.getLogger(AioAcceptHandler.class);
    private SSLContext sslContext;
    private AsynchronousSocketChannel socket;
    private SSLEngine sslEngine;
    private ByteBuffer myAppData;
    private ByteBuffer myNetData;
    private ByteBuffer peerAppData;
    private ByteBuffer peerNetData;
    private String remoteAddr;

    public AsynSSLSocketChannel(SSLContext sslContext, AsynchronousSocketChannel socket) throws Exception {
        super();
        this.sslContext = sslContext;
        this.socket = socket;
        this.remoteAddr = socket.getRemoteAddress().toString();
        logInfo("Initiating SSL connection...");
        createSSLEngine();
        createBuffers();
        doHandShake();
    }

    public String getRemoteAddr() {
        return remoteAddr;
    }

    private void createSSLEngine() throws UnrecoverableKeyException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException, CertificateException, IOException,
                                    Exception {
        sslEngine = sslContext.createSSLEngine();
        sslEngine.setUseClientMode(false);//work in a server mode  
        sslEngine.setNeedClientAuth(true);//need client authentication 
    }

    private void createBuffers() {
        // Create byte buffers to use for holding application and encoded data
        SSLSession session = sslEngine.getSession();
        myAppData = ByteBuffer.allocate(session.getApplicationBufferSize());
        myNetData = ByteBuffer.allocate(session.getPacketBufferSize());
        peerAppData = ByteBuffer.allocate(session.getApplicationBufferSize());
        peerNetData = ByteBuffer.allocate(session.getPacketBufferSize());
    }

    private void doHandShake() throws Exception {
        sslEngine.beginHandshake();//explicitly begin the handshake  
        HandshakeStatus hsStatus = sslEngine.getHandshakeStatus();
        boolean handshakeDone = false;
        while (!handshakeDone) {
            SSLEngineResult res = null;
            switch (hsStatus) {
                case FINISHED:
                    //the status become FINISHED only when the ssl handshake is finished  
                    //but we still need to send data, so do nothing here  
                    logInfo("Handshake done. hsStatus = " + hsStatus);
                    handshakeDone = true;
                    break;
                case NEED_TASK:
                    //do the delegate task if there is some extra work such as checking the keystore during the handshake  
                    hsStatus = doTask();
                    break;
                case NEED_UNWRAP:
                    //unwrap means unwrap the ssl packet to get ssl handshake information  
                    int cnt = socket.read(peerNetData).get();
                    logInfo("Bytes received:" + cnt);
                    peerNetData.flip();
                    res = sslEngine.unwrap(peerNetData, peerAppData);
                    peerNetData.compact();
                    handleErrorStatus(res);
                    hsStatus = res.getHandshakeStatus();
                    break;
                case NEED_WRAP:
                    //wrap means wrap the app packet into an ssl packet to add ssl handshake information  
                    // Empty the local network packet buffer.
                    myNetData.clear();
                    myAppData.flip();
                    res = sslEngine.wrap(myAppData, myNetData);
                    myAppData.compact();
                    handleErrorStatus(res);
                    flush();
                    hsStatus = res.getHandshakeStatus();
                    break;
                case NOT_HANDSHAKING:
                    //now it is not in a handshake or say byebye status. here it means handshake is over and ready for ssl talk  
                    logInfo("Handshake done. hsStatus = " + hsStatus);
                    handshakeDone = true;
                    break;
            }
        }
    }


    private void handleErrorStatus(SSLEngineResult result) throws Exception {
        switch (result.getStatus()) {
            case BUFFER_UNDERFLOW:
            case BUFFER_OVERFLOW:
            case CLOSED:
                throw new Exception("SSL Engine handling error, status : " + result.getStatus());
            default:
                break;
        }
    }

    private void flush() throws InterruptedException, ExecutionException {
        myNetData.flip();
        int countOut = 0;
        int count;

        while (myNetData.hasRemaining()) {
            count = socket.write(myNetData).get();
            countOut += count;
        }
        logInfo("Bytes sent : " + countOut);
        myNetData.compact();
    }

    private HandshakeStatus doTask() {
        Runnable runnable;
        while ((runnable = sslEngine.getDelegatedTask()) != null) {
            logInfo("running delegated task...");
            runnable.run();
        }
        return sslEngine.getHandshakeStatus();
    }

    public <A> void read(ByteBuffer dst, A attachment, CompletionHandler<Integer, ? super A> handler) {
        if (sslEngine.isInboundDone()) {
            // We can skip the read operation as the SSLEngine is closed,
            // instead, propagate EOF one level up
            logInfo("Read reaches the end because SSLEngine is closed.");
            handler.completed(-1, attachment);
            return;
        }

        peerAppData.clear();

        // Read from the channel
        socket.read(peerNetData, attachment, new CompletionHandler<Integer, A>() {
            @Override
            public void completed(Integer cnt, A attachment) {
                SSLEngineResult result;
                try {
                    peerNetData.flip();
                    result = sslEngine.unwrap(peerNetData, peerAppData);
                    peerNetData.compact();
                    // Process the engineResult.Status
                    handleErrorStatus(result);
                    peerAppData.flip();
                    dst.put(peerAppData);
                    handler.completed(dst.limit(), attachment);
                } catch (Exception e) {
                    logWarn("SSL Exception while reading data from the socket", e);
                    handler.completed(0, attachment);
                }
            }

            @Override
            public void failed(Throwable t, A attachment) {
                logWarn("Failed to read data from the socket", t);
                handler.completed(0, attachment);
            }
        });
    }

    private void logInfo(String msg) {
        logger.info("[" + getRemoteAddr() + "] " + msg);
    }

    private void logWarn(String msg, Throwable e) {
        logger.warn("[" + getRemoteAddr() + "] " + msg, e);
    }
}
