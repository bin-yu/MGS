package com.yyy.server.door.facade;

import com.yyy.server.door.repo.Door;

public class DoorSystemFactory {
    public static DoorSystem createInstance(Door door) {
        if ("TCP".equalsIgnoreCase(door.getProtocol())) {
            return new TcpFC8800(door.getSn(), door.getIp(), door.getPort());
        }
        else if ("MOCK".equalsIgnoreCase(door.getProtocol())) {
            return new MockFC8800(door.getSn());
        }
        throw new IllegalArgumentException("Invalid door protocol:" + door.getProtocol());
    }
}
