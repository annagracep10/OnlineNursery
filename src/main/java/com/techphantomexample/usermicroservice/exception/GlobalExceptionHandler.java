package com.techphantomexample.usermicroservice.exception;

import com.techphantomexample.usermicroservice.controller.CreateResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserOperationException.class)
    public ResponseEntity<CreateResponse> handleUserOperationException(UserOperationException ex, WebRequest request) {
        CreateResponse createResponse = new CreateResponse(ex.getMessage(), HttpStatus.BAD_REQUEST.value(),null);
        return new ResponseEntity<CreateResponse>(createResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CreateResponse> handleGlobalException(Exception ex, WebRequest request) {
        CreateResponse createResponse = new CreateResponse("An unexpected error occurred: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(),null);
        return new ResponseEntity<>(createResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
