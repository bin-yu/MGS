package com.yyy.proxy.common.client;

import java.util.Arrays;

import javax.xml.bind.DatatypeConverter;

import com.yyy.proxy.common.Command;

public class DoorResponseCommand extends Command {
	protected String doorSecret;
	protected byte[] body;

	public DoorResponseCommand() {
		super();
	}

	public DoorResponseCommand(String doorSecret, byte[] body) {
		super();
		this.doorSecret = doorSecret;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(body);
		result = prime * result + ((doorSecret == null) ? 0 : doorSecret.hashCode());
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
		DoorResponseCommand other = (DoorResponseCommand) obj;
		if (!Arrays.equals(body, other.body))
			return false;
		if (doorSecret == null) {
			if (other.doorSecret != null)
				return false;
		} else if (!doorSecret.equals(other.doorSecret))
			return false;
		return true;
	}



	@Override
	public String toString() {
		return "DoorResponseCommand [doorSecret=" + doorSecret + ", body=" + DatatypeConverter.printHexBinary(body) + "]";
	}

	@Override
	public String getSecret() {
		// TODO Auto-generated method stub
		return doorSecret;
	}

	
}
