package com.user.microservices.msvc_user.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiResponse {
private String message;
private Object data;
}
