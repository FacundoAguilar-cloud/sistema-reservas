package com.user.microservices.msvc_user.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.user.microservices.msvc_user.commons.UserDto;
import com.user.microservices.msvc_user.entities.User;
import com.user.microservices.msvc_user.exceptions.ResourceAlreadyExistExcp;
import com.user.microservices.msvc_user.exceptions.ResourceNotFoundException;
import com.user.microservices.msvc_user.request.newUserRequest;
import com.user.microservices.msvc_user.request.updateUserRequest;
import com.user.microservices.msvc_user.response.ApiResponse;
import com.user.microservices.msvc_user.services.UserServiceIMPL;


import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;






@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {
private final UserServiceIMPL userServiceIMPL;


@GetMapping("/get-all")
public ResponseEntity <ApiResponse> getAllUsers(){
    try {
        List <User> userList = userServiceIMPL.getAllUsers();
        List <UserDto> dtoList = userList.
        stream()
        .map(userServiceIMPL::convertToDto)
        .collect(Collectors.toList());
        return ResponseEntity.ok(new ApiResponse("Users retrieved successfully", dtoList));
        
    } catch (ResourceNotFoundException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse("There are not users to retrieve", e));
    }
}

@GetMapping("/get-by-id/{userId}")
public ResponseEntity <ApiResponse> getUserById(@PathVariable Long userId){
    try {
         User user = userServiceIMPL.getUserById(userId);
         UserDto userDto = userServiceIMPL.convertToDto(user);
        return ResponseEntity.ok(new ApiResponse("User retrieved successfully", userDto));
        
    } catch (ResourceNotFoundException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse("User not found, please try again", e));
    }
}

@PostMapping("/create") 
//@PreAuthorize("hasRole('ROLE_ADMIN')")
public ResponseEntity<ApiResponse> createNewUser(@RequestBody newUserRequest request) {
    try {
        User user = userServiceIMPL.createUser(request);
        UserDto userDto = userServiceIMPL.convertToDto(user);
        return ResponseEntity.ok(new ApiResponse("User created successfully", userDto));
    } catch (ResourceAlreadyExistExcp e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse("User already exist", null));
    }
}

@PutMapping("/update/{userId}")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public ResponseEntity<ApiResponse> updateUser(@PathVariable Long userId, @RequestBody updateUserRequest request){
try {
    User user = userServiceIMPL.updateUser(userId, request);
    UserDto userDto = userServiceIMPL.convertToDto(user);
    return ResponseEntity.ok(new ApiResponse("User updated successfully", userDto));
} catch (ResourceNotFoundException e) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse("An error occurred, please check credentials and try again", null));
}
}





}
