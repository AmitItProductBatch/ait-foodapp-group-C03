package com.ait.app.exception;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
            ResourceNotFoundException ex, WebRequest request) {

        return buildResponse(HttpStatus.NOT_FOUND,ex.getMessage(),request);
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleResourceAlreadyExists(ResourceAlreadyExistsException ex, WebRequest request) {

        return buildResponse(HttpStatus.CONFLICT,ex.getMessage(),request);
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRequest(InvalidRequestException ex, WebRequest request) {

        return buildResponse(HttpStatus.BAD_REQUEST,ex.getMessage(),request);
    }

    @ExceptionHandler(UnauthorizedActionException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedAction(UnauthorizedActionException ex, WebRequest request) {

        return buildResponse(HttpStatus.FORBIDDEN,ex.getMessage(),request);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentials(InvalidCredentialsException ex, WebRequest request) {

        return buildResponse(HttpStatus.UNAUTHORIZED,ex.getMessage(),request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,HttpHeaders headers,HttpStatusCode status,WebRequest request) {

        List<String> fieldErrors = ex.getBindingResult().getFieldErrors().stream().map(FieldError::getDefaultMessage).collect(Collectors.toList());

        ErrorResponse body = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),HttpStatus.BAD_REQUEST.getReasonPhrase(),"Validation failed",path(request),fieldErrors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex,WebRequest request) {

        return buildResponse(HttpStatus.BAD_REQUEST,ex.getMessage(),request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,HttpHeaders headers,HttpStatusCode status,WebRequest request) {

        ErrorResponse body = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),HttpStatus.BAD_REQUEST.getReasonPhrase(),"Malformed JSON request body",path(request));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAnyOtherException(Exception ex,WebRequest request) {


        ex.printStackTrace();

        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR,ex.getMessage(),request);
    }


    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status,String message,WebRequest request) {

        ErrorResponse body = new ErrorResponse(status.value(),status.getReasonPhrase(),message,path(request));

        return ResponseEntity.status(status).body(body);
    }

    private String path(WebRequest request) {

        String description = request.getDescription(false);

        if (description.startsWith("uri=")) {
            return description.substring(4);
        }

        return description;
    }
}