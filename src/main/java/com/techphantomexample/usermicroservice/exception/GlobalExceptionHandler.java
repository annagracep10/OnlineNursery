package com.techphantomexample.usermicroservice.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.techphantomexample.usermicroservice.model.CreateResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(UserOperationException.class)
    public ResponseEntity<CreateResponse> handleUserOperationException(UserOperationException ex) {
        CreateResponse createResponse = new CreateResponse(ex.getMessage(), HttpStatus.BAD_REQUEST.value(),null);
        return new ResponseEntity<CreateResponse>(createResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(JsonProcessingException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public String handleJsonProcessingException(JsonProcessingException ex) {
        logger.error("JsonProcessingException occurred: {}", ex.getMessage());
        return "Error processing JSON: " + ex.getMessage();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CreateResponse> handleGlobalException(Exception ex, WebRequest request) {
        CreateResponse createResponse = new CreateResponse("An unexpected error occurred: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(),null);
        return new ResponseEntity<>(createResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
