package io.cfp.auth.dto;

/**
 * Error send to the user
 */
public class ErrorRes {

	/** Error name */
	private String error;

	private String msg;

	public ErrorRes(String error, String msg) {
		this.error = error;
		this.msg = msg;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
}
