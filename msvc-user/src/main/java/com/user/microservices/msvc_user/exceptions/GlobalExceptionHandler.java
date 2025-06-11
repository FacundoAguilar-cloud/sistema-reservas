package com.user.microservices.msvc_user.exceptions;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.HttpStatus;

import com.user.microservices.msvc_user.response.ApiResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(ResourceAlreadyExistExcp.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ApiResponse handleExists(ResourceAlreadyExistExcp ex) {
    return new ApiResponse(null, ex.getMessage());
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ApiResponse handleNotFound(ResourceNotFoundException ex) {
    return new ApiResponse(null, ex.getMessage());
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ApiResponse handleOthers(Exception ex) {
    return new ApiResponse(null, "An error occurred");



  }
}
