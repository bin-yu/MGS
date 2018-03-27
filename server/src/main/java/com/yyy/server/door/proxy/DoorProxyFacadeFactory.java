package com.yyy.server.door.proxy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import com.yyy.server.door.proxy.aio.AioDoorProxyFacade;
import com.yyy.server.door.proxy.bio.BioDoorProxyFacade;
import com.yyy.server.door.proxy.bio.BioTcpServer;

@Configuration
public class DoorProxyFacadeFactory {
    private static final String MODE_BIO = "BIO";
    private static final String MODE_AIO = "AIO";

    @Value("${mgs.door.proxy-server.io-mode}")
    private String mode = MODE_AIO;

    @Bean
    public DoorProxyFacade doorProxyFacade() {
        switch (mode) {
            case MODE_AIO:
                return new AioDoorProxyFacade();
            default:
                return new BioDoorProxyFacade();
        }
    }
}
