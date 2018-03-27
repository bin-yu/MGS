package com.yyy.server.door.proxy;

public interface DoorProxyFacade {
    public DoorProxy getDoorConnection(String doorSecret) throws DoorCommandException;
}
