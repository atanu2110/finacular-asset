package com.finadv.assets.error;

import org.springframework.http.HttpStatus;

/**
 * @author atanu
 *
 */
public class RestServiceException extends RuntimeException {

	/**
		 * 
		 */
	private static final long serialVersionUID = -8333304185895147621L;
	private final HttpStatus httpStatus;

	/**
	 * 
	 */
	public RestServiceException() {
		super();
		this.httpStatus = null;
	}

	/**
	 * 
	 */
	public RestServiceException(HttpStatus httpStatus, String message) {
		super(message);
		this.httpStatus = httpStatus;
	}

	public RestServiceException(HttpStatus httpStatus, String msg, Exception e) {
		super(msg, e);
		this.httpStatus = httpStatus;
	}

	public RestServiceException(HttpStatus httpStatus, Exception e) {
		super(e);
		this.httpStatus = httpStatus;
	}

	public HttpStatus getHttpStatus() {
		return httpStatus;
	}
}
