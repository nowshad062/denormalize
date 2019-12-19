package com.pvamu.den.model;

public class ResponseBody {
	private int status = 200 ;
    private String message = "Successfully Generated Record";
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
}
