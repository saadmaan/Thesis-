package com.sachetan.domains;

/**
 * @author rakib (ansaryfantastic@gmail.com
 */

public class GenericResponse {
	private int status;
	private String message;

	public GenericResponse(int status, String errorMessage) {
		this.status = status;
		this.message = errorMessage;
	}

	public int getStatus() {
		return status;
	}

	public String getMessage() {
		return message;
	}
}
