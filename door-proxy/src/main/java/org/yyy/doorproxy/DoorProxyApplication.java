package org.yyy.doorproxy;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class DoorProxyApplication {

    public static void main(String[] args) throws Exception {
        
        ConfigurableApplicationContext ctx = new AnnotationConfigApplicationContext(SpringConfig.class);
        try {
            DoorProxy proxy = ctx.getBean(DoorProxy.class);
            proxy.start();
        } finally {
            ctx.close();
        }
        System.exit(0);
    }
}
