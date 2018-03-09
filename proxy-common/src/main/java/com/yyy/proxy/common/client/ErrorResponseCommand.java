package com.yyy.proxy.common.client;

public class ErrorResponseCommand extends DoorResponseCommand {
	private String error;

	public ErrorResponseCommand() {
		super();
	}

	public ErrorResponseCommand(String doorSecret, String error) {
		super(doorSecret,null);
		this.error = error;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((error == null) ? 0 : error.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ErrorResponseCommand other = (ErrorResponseCommand) obj;
		if (error == null) {
			if (other.error != null)
				return false;
		} else if (!error.equals(other.error))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ErrorResponseCommand [doorSecret=" + doorSecret + ", error=" + error + "]";
	}


	
}
