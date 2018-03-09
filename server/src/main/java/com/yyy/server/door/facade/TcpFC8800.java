package com.yyy.server.door.facade;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TcpFC8800 extends AbstractFC8800 {
    private static Logger logger = LoggerFactory.getLogger(TcpFC8800.class);
    protected String ip;
    protected int port;
    private Socket socket;
    private InputStream in;
    private OutputStream out;

    public TcpFC8800(String sn, String password, String ip, int port) {
        super(sn, password);
        this.ip = ip;
        this.port = port;
    }

    public TcpFC8800(String sn, String ip, int port) {
        super(sn);
        this.ip = ip;
        this.port = port;
    }

    @Override
    protected void connect() throws UnknownHostException, IOException {
        socket = new Socket(ip, port);
        in = socket.getInputStream();
        out = socket.getOutputStream();
        logger.info("[" + sn + "]DOOR CONNECTED!");
    }

    @Override
    protected void disconnect() {
        try {
            out.close();
            in.close();
            socket.close();
        } catch (IOException e) {
            logger.warn("failed to disconnect to the door!" + ip, e);
        } finally {
            logger.info("[" + sn + "]DOOR DISCONNECTED!");
        }

    }

    @Override
    protected void sendRequest(byte[] request) throws IOException {
        logger.info("[" + sn + "]DOOR SEND DATA:" + ConverterTool.Byte2Hex(request));
        out.write(request);
    }

    @Override
    protected int receiveResponse(byte[] buffer) throws IOException {
        int cnt = in.read(buffer);
        if (cnt > 0) {
            logger.info("[" + sn + "]DOOR RECV DATA:" + ConverterTool.Byte2Hex(buffer, cnt));
        }
        return cnt;
    }

}
