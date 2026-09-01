package com.msvc.catalog.shared.exception;

import com.msvc.catalog.shared.responses.ApiErrorResponse;
import com.msvc.catalog.shared.responses.ResponseFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final ResponseFactory responseFactory;

    public GlobalExceptionHandler(ResponseFactory responseFactory) {
        this.responseFactory = responseFactory;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(responseFactory.error(HttpStatus.NOT_FOUND, ex.getMessage()));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiErrorResponse> handleBusinessException(BusinessException ex) {

        return ResponseEntity.badRequest()
                .body(responseFactory.error(HttpStatus.BAD_REQUEST, ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleException(Exception ex) {

        return ResponseEntity.internalServerError()
                .body(responseFactory.error(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "An unexpected error occurred.",
                        List.of(ex.getMessage())
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex
    ) {
        List<String> details = ex
                .getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .toList();

        return ResponseEntity.badRequest()
                .body(
                        responseFactory.error(
                                HttpStatus.BAD_REQUEST,
                                "Request validation failed.",
                                details
                        )
                );
    }

}
