package com.yyy.server.door.proxy.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.yyy.server.door.proxy.DoorProxyFacade;

/**
 * AIO异步socket通讯，分成 用于服务端的socekt与用于客户端的socket，当然这两者都是
 * 异步的。两者使用时，都用到了同样的异步通道管理器，异步通道管理器通过线程池管理。
 *    异步通道管理器，可以生成服务端socket与客户端socket。 * 
 *    使用服务端socket或客户端socket都需要一个操作处理器（CompletionHandler）
 * 
 * 当有信息时异步通道管理器会把 相关信息传递给操作作处理器。 * 
 *    操作处理器的方法是同一方法，但方法的参数是泛型，随着调用它的方法不同而改变。
 *    在AIO中，CompletionHandler这个操作处理器方法，是个泛型接口，当回调函数用。
 * 使用CompletionHandler的方法，约定是把该方法前一个参数实例传递给A型参数
 * （attachment），CompletionHandler的另一个参数将是存有该方法的使用情况的实例。
 * 
 */
@Service
public class AioTcpServer {
    private static final String KEYSTORE = "/keystore.jks";
    private Logger logger = LoggerFactory.getLogger(AioTcpServer.class);
    @Autowired
    private DoorProxyFacade doorProxyFac;
    @Value("${mgs.door.proxy-server.accept-thread-cnt}")
    private int acceptThreadCnt = 20;
    @Value("${mgs.door.proxy-server.port}")
    private int port = 8443;
    @Value("${mgs.door.proxy-server.keystore.password}")
    private String ksPass = "changeme";
    private AsynchronousChannelGroup asyncChannelGroup;
    private AsynchronousServerSocketChannel listener;
    private SSLEngine sslEngine;

    @PostConstruct
    public void init() throws Exception {
        //创建线程池
        ExecutorService executor = Executors.newFixedThreadPool(acceptThreadCnt);
        //异步通道管理器
        asyncChannelGroup = AsynchronousChannelGroup.withThreadPool(executor);
        //创建 用在服务端的异步Socket.以下简称服务器socket。
        //异步通道管理器，会把服务端所用到的相关参数
        InetSocketAddress addr = new InetSocketAddress(port);
        listener = AsynchronousServerSocketChannel.open(asyncChannelGroup).bind(addr);
        //为服务端socket指定接收操作对象.accept原型是：
        //accept(A attachment, CompletionHandler<AsynchronousSocketChannel, ? super A> handler)
        //也就是这里的CompletionHandler的A型参数是实际调用accept方法的第一个参数
        //即是listener。另一个参数V，就是原型中的客户端socket
        logger.info("Start listening to " + addr);
        listener.accept(listener, new AioAcceptHandler(initSSLContext()));


    }

    private SSLContext initSSLContext() throws NoSuchAlgorithmException, KeyStoreException, IOException, CertificateException, UnrecoverableKeyException, Exception,
                                    KeyManagementException {
        logger.info("Creating SSL Context...");
        SSLContext sc = SSLContext.getInstance("TLSv1.2");
        SecureRandom rand = new SecureRandom();
        KeyStore ks = loadKeystore();
        KeyManager[] km = getKeyManagers(ks);
        TrustManager[] tm = getTrustManagers(ks);
        sc.init(km, tm, rand);
        logger.info("SSL Context initialized.");
        return sc;
    }

    private KeyManager[] getKeyManagers(KeyStore ks) throws NoSuchAlgorithmException, UnrecoverableKeyException, KeyStoreException, CertificateException, IOException {
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(ks, ksPass.toCharArray());
        return kmf.getKeyManagers();
    }

    private KeyStore loadKeystore() throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
        logger.info("Loading keystore :" + KEYSTORE);
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(this.getClass().getResourceAsStream(KEYSTORE), ksPass.toCharArray());
        logger.info("Keystore entry size : " + ks.size());
        return ks;
    }

    private TrustManager[] getTrustManagers(KeyStore ks) throws Exception {
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(ks);
        return tmf.getTrustManagers();
    }

    public static void main(String[] args) throws Exception {
        AioTcpServer server = new AioTcpServer();
        server.init();
        System.in.read();
    }
}
