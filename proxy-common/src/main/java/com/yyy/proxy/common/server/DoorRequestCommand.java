package com.yyy.proxy.common.server;

import java.util.Arrays;

import javax.xml.bind.DatatypeConverter;

import com.yyy.proxy.common.Command;

public class DoorRequestCommand extends Command {
	public static final String PROTOCOL_TCP = "TCP";
	private String doorSecret;
	private String protocol = PROTOCOL_TCP;
	private String doorIp;
	private int doorPort;
	private byte[] body;

	public DoorRequestCommand() {
		super();
	}

	public DoorRequestCommand(String doorSecret, String doorIp, int doorPort, String protocol, byte[] body) {
		super();
		this.doorSecret = doorSecret;
		this.protocol = protocol;
		this.doorIp = doorIp;
		this.doorPort = doorPort;
		this.body = body;
	}

	public byte[] getBody() {
		return body;
	}

	public void setBody(byte[] body) {
		this.body = body;
	}

	public String getDoorSecret() {
		return doorSecret;
	}

	public void setDoorSecret(String doorSecret) {
		this.doorSecret = doorSecret;
	}

	public String getDoorIp() {
		return doorIp;
	}

	public void setDoorIp(String doorIp) {
		this.doorIp = doorIp;
	}

	public int getDoorPort() {
		return doorPort;
	}

	public void setDoorPort(int doorPort) {
		this.doorPort = doorPort;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(body);
		result = prime * result + ((doorIp == null) ? 0 : doorIp.hashCode());
		result = prime * result + doorPort;
		result = prime * result + ((doorSecret == null) ? 0 : doorSecret.hashCode());
		result = prime * result + ((protocol == null) ? 0 : protocol.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DoorRequestCommand other = (DoorRequestCommand) obj;
		if (!Arrays.equals(body, other.body))
			return false;
		if (doorIp == null) {
			if (other.doorIp != null)
				return false;
		} else if (!doorIp.equals(other.doorIp))
			return false;
		if (doorPort != other.doorPort)
			return false;
		if (doorSecret == null) {
			if (other.doorSecret != null)
				return false;
		} else if (!doorSecret.equals(other.doorSecret))
			return false;
		if (protocol == null) {
			if (other.protocol != null)
				return false;
		} else if (!protocol.equals(other.protocol))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "DoorRequestCommand [doorSecret=" + doorSecret + ", protocol=" + protocol + ", doorIp=" + doorIp
				+ ", doorPort=" + doorPort + ", body=" + DatatypeConverter.printHexBinary(body) + "]";
	}

	@Override
	public String getSecret() {
		return doorSecret;
	}

}
