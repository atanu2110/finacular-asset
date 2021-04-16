package com.finadv.assets.error;

import java.util.stream.Stream;

import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.finadv.assets.dto.ErrorResponseDto;

/**
 * @author atanu
 *
 */

@ControllerAdvice
public class ApiErrorResponseHandler extends ResponseEntityExceptionHandler {

	private static final Logger LOG = LoggerFactory.getLogger(ApiErrorResponseHandler.class);

	@ExceptionHandler(ConstraintViolationException.class)
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public final @ResponseBody ResponseEntity<Object> handleConstraintViolationException(
			ConstraintViolationException ex, WebRequest request) {

		LOG.error("ApiResponseEntityHandler.handleConstraintViolationException() {}", ex.getMessage());

		return new ResponseEntity<>(new ErrorResponseDto(ex.getMessage(), 400), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(RestServiceException.class)
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public final @ResponseBody ResponseEntity<Object> handleConstraintInvalidPasswordException(RestServiceException ex,
			WebRequest request) {

		LOG.error("ApiResponseEntityHandler.handleConstraintInvalidPasswordException() {}", ex.getMessage());

		return new ResponseEntity<>(
				new ErrorResponseDto(Stream.of(ex.getMessage().split(":")).reduce((first, last) -> last).get(), 400),
				HttpStatus.BAD_REQUEST);

	}

}
