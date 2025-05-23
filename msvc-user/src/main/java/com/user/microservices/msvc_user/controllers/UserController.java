package com.user.microservices.msvc_user.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.user.microservices.msvc_user.entities.User;
import com.user.microservices.msvc_user.exceptions.ResourceNotFoundException;
import com.user.microservices.msvc_user.request.newUserRequest;
import com.user.microservices.msvc_user.response.ApiResponse;
import com.user.microservices.msvc_user.services.UserServiceIMPL;


import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;




@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {
private final UserServiceIMPL userServiceIMPL;


@GetMapping("/get-all")
public ResponseEntity <ApiResponse> getAllUsers(){
    try {
        List <User> userList = userServiceIMPL.getAllUsers();
        return ResponseEntity.ok(new ApiResponse("Users retrieved successfully", userList));
        
    } catch (ResourceNotFoundException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse("There are not users to retrieve", e));
    }
}

@GetMapping("/get-by-id")
public ResponseEntity <ApiResponse> getUserById(@PathVariable Long userId){
    try {
         User user = userServiceIMPL.getUserById(userId);
        return ResponseEntity.ok(new ApiResponse("User retrieved successfully", user));
        
    } catch (ResourceNotFoundException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse("User not found, please try again", e));
    }
}
@PostMapping("/create")
public ResponseEntity<ApiResponse> createNewUser(newUserRequest request) {
    try {
        User user = userServiceIMPL.createUser(request);
        return null;
    } catch (Exception e) {
        return null;
    }
    
}





}
