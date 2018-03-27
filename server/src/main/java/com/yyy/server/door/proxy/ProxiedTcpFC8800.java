package com.yyy.server.door.proxy;

import java.io.IOException;
import java.net.UnknownHostException;

import org.springframework.beans.factory.annotation.Autowired;

import com.yyy.proxy.common.client.DoorResponseCommand;
import com.yyy.proxy.common.client.ErrorResponseCommand;
import com.yyy.proxy.common.server.DoorRequestCommand;
import com.yyy.server.door.facade.AbstractFC8800;
import com.yyy.server.door.repo.Door;

public class ProxiedTcpFC8800 extends AbstractFC8800 {
	@Autowired
	private BioDoorProxyFacade proxy;
	private Door door;
	private DoorResponseCommand resp;
	private int readPos = 0;
    private DoorProxy doorConn;

	public ProxiedTcpFC8800(Door door) {
		super(door.getSn(), door.getPassword());
		this.door = door;
	}

	@Override
	protected void connect() throws UnknownHostException, IOException {
		try {
            doorConn = proxy.getDoorConnection(door.getSecret());
		} catch (DoorCommandException e) {
			throw new IOException(e.getMessage(), e);
		}
	}

	@Override
	protected void disconnect() {

	}

	@Override
	protected void sendRequest(byte[] request) throws IOException {
		try {
			resp = null;
			readPos = 0;
            resp = doorConn.sendCommand(new DoorRequestCommand(door.getSecret(), door.getIp(),
					door.getPort(), door.getProtocol(), request));
			if (resp instanceof ErrorResponseCommand) {
				throw new IOException(((ErrorResponseCommand) resp).getError());
			}
		} catch (DoorCommandException e) {
			throw new IOException(e.getMessage(), e);
		}

	}

	@Override
	protected int receiveResponse(byte[] buffer) throws IOException {
		if (resp == null) {
			throw new IOException("No response");
		}
		int cnt = resp.getBody().length - readPos;
		if (cnt > buffer.length) {
			cnt = buffer.length;
		}
		if (cnt == 0) {
			return -1;// no more data
		}
		System.arraycopy(resp.getBody(), readPos, buffer, 0, cnt);
		readPos += cnt;
		return cnt;
	}

}
