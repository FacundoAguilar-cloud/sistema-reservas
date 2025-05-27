package com.security.microservices.msvc_security.exceptions;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.HttpStatus;

import com.security.microservices.msvc_security.response.ApiResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(ResourceAlreadyExistExcp.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ApiResponse handleExists(ResourceAlreadyExistExcp ex) {
    return new ApiResponse(null, ex.getMessage());
  }

  @ExceptionHandler(ResourceNotFoundExcp.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ApiResponse handleNotFound(ResourceNotFoundExcp ex) {
    return new ApiResponse(null, ex.getMessage());
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ApiResponse handleOthers(Exception ex) {
    return new ApiResponse(null, "An error occurred");


}
}