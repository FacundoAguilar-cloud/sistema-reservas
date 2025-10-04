package com.payments.microservices.msvc_payments.processing;

import org.springframework.stereotype.Service;

import com.payments.microservices.msvc_payments.client.UserClient;
import com.payments.microservices.msvc_payments.dto.UserDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDataService {
private final UserClient userClient;


public String getUserEmail(Long userId){
try {
    UserDto user = userClient.getUserById(userId);
    return user != null ? user.getEmail() : null;
} catch (Exception e) {
    log.warn("Could not retrieve user email for userId" + userId, e);
    return null;
}
}


public UserDto getUserData(Long userId){
    try {
        return userClient.getUserById(userId);
    } catch (Exception e) {
       log.warn("Could not retrieve user data for userId" +userId, e);
       return null;
    }
}
}
