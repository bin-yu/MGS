package com.yyy.server.door.facade;

import java.util.function.Function;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import com.yyy.server.door.proxy.ProxiedTcpFC8800;
import com.yyy.server.door.repo.Door;
@Configuration
public class DoorSystemFactory {
	@Bean
    public Function<Door, DoorSystem> doorSysFactory() {
        return door -> doorSystem(door); // or this::thing
    } 
    @Bean
    @Scope("prototype")
    public DoorSystem doorSystem(Door door) {
        if ("TCP".equalsIgnoreCase(door.getProtocol())) {
        	return new ProxiedTcpFC8800(door);
            //return new TcpFC8800(door.getSn(),door.getPassword(),door.getIp(), door.getPort());
        }
        else if ("MOCK".equalsIgnoreCase(door.getProtocol())) {
            return new MockFC8800(door.getSn());
        }
        throw new IllegalArgumentException("Invalid door protocol:" + door.getProtocol());
    }
   
}
