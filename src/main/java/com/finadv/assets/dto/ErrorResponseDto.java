package com.finadv.assets.dto;

/**
 * @author atanu
 *
 */
public class ErrorResponseDto {

	private String error;

	private int code;

	public ErrorResponseDto(String error, int code) {
		super();
		this.error = error;
		this.code = code;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

}
