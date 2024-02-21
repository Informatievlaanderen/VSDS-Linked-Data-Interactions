package be.vlaanderen.informatievlaanderen.ldes.ldio.controller;

import be.vlaanderen.informatievlaanderen.ldes.ldio.exception.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class PipelineExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(value = {PipelineAlreadyExistsException.class, InvalidPipelineNameException.class,
			InvalidComponentException.class, LdiAdapterMissingException.class, ConfigPropertyMissingException.class})
	public ResponseEntity<Object> handleConflict(RuntimeException ex, WebRequest request) {
		return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
	}

	@ExceptionHandler(value = {PipelineDoesNotExistsException.class})
	public ResponseEntity<Object> handleNotFound(RuntimeException ex, WebRequest request) {
		return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(), HttpStatus.NOT_FOUND, request);
	}
}
