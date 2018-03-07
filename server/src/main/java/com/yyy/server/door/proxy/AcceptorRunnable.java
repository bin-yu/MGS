package com.yyy.server.door.proxy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AcceptorRunnable implements Runnable {

    private Logger logger = LoggerFactory.getLogger(ProxyServer.class);
    private Selector acceptSelector;

    public AcceptorRunnable(Selector acceptSelector2) {
        this.acceptSelector = acceptSelector2;
    }
    @Override
    public void run() {
        int keysAdded = 0;

        // Here's where everything happens. The select method will
        // return when any operations registered above have occurred, the
        // thread has been interrupted, etc.
        try {
            while ((keysAdded = acceptSelector.select()) > 0) {
                // Someone is ready for I/O, get the ready keys
                Set readyKeys = acceptSelector.selectedKeys();
                Iterator i = readyKeys.iterator();

                // Walk through the ready keys collection and process date requests.
                while (i.hasNext()) {
                    SelectionKey sk = (SelectionKey) i.next();
                    i.remove();
                    // The key indexes into the selector so you
                    // can retrieve the socket that's ready for I/O
                    ServerSocketChannel nextReady = (ServerSocketChannel) sk.channel();
                    // Accept the request 
                    try (Socket s = nextReady.accept().socket()) {
                        doWithSocket(s);
                    }
                }
            }
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void doWithSocket(Socket s) throws IOException, UnsupportedEncodingException {
        String sendMsg = "Welcome, " + s.getRemoteSocketAddress();
        logger.info("Sending message: " + sendMsg);
        try (PrintWriter out = new PrintWriter(s.getOutputStream(), true)) {
            out.println(sendMsg);
            try (BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream(), "UTF-8"))) {
                String msg = in.readLine();
                logger.info("Message received : " + msg);
            }
        }

    }
}
