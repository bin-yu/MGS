package com.yyy.proxy.common.client;

import java.util.Arrays;

import com.yyy.proxy.common.Command;

public class RegisterCommand extends Command {
	private String[] secrets;

	public RegisterCommand() {
		super();
	}

	public RegisterCommand(String... secrets) {
		super();
		this.secrets = secrets;
	}

	public String[] getSecrets() {
		return secrets;
	}

	public void setSecrets(String[] secrets) {
		this.secrets = secrets;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(secrets);
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
		RegisterCommand other = (RegisterCommand) obj;
		if (!Arrays.equals(secrets, other.secrets))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "RegisterCommand [secrets=" + Arrays.toString(secrets) + "]";
	}

	@Override
	public String getSecret() {
		return Arrays.toString(secrets);
	}

}
