package com.yyy.server.door.proxy;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ProxyServer {
    private Logger logger = LoggerFactory.getLogger(ProxyServer.class);
    @Value("${mgs.door.proxy-server.thread-cnt}")
    private int threadCnt;
    @Value("${mgs.door.proxy-server.port}")
    private int port;
    private ExecutorService pool;

    @PostConstruct
    public void start() throws IOException {
        // Selector for incoming time requests
        Selector acceptSelector = SelectorProvider.provider().openSelector();

        // Create a new server socket and set to non blocking mode
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);

        // Bind the server socket to the local host and port
        InetAddress lh = InetAddress.getLocalHost();
        logger.info("Listening to " + lh.toString() + ":" + port + "...");
        InetSocketAddress isa = new InetSocketAddress(lh, port);
        ssc.socket().bind(isa);

        // Register accepts on the server socket with the selector. This
        // step tells the selector that the socket wants to be put on the
        // ready list when accept operations occur, so allowing multiplexed
        // non-blocking I/O to take place.
        SelectionKey acceptKey = ssc.register(acceptSelector, SelectionKey.OP_ACCEPT);

        pool = Executors.newFixedThreadPool(1, new ThreadFactory() {

            @Override
            public Thread newThread(Runnable r) {

                return new Thread(r, "Proxy Connection Acceptor");
            }

        });
        pool.execute(new AcceptorRunnable(acceptSelector));

        /*try {
            ExecutorService executor = Executors.newFixedThreadPool(threadCnt);
            AsynchronousChannelGroup asyncChannelGroup = AsynchronousChannelGroup.withThreadPool(executor);
            listener = AsynchronousServerSocketChannel.open(asyncChannelGroup).bind(new InetSocketAddress(port), 100);
            listener.accept(listener, new ProxyAcceptHandler());
        } catch (IOException e) {
            logger.warn("Failed to start door proxy server!!!", e);
        }*/
    }


}
