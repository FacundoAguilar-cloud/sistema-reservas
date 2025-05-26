package com.user.microservices.msvc_user.services;

import java.util.List;
import java.util.Optional;

import com.user.microservices.msvc_user.commons.UserDto;
import com.user.microservices.msvc_user.entities.Role;
import com.user.microservices.msvc_user.entities.User;
import com.user.microservices.msvc_user.request.newUserRequest;
import com.user.microservices.msvc_user.request.updateUserRequest;

public interface UserServiceIMPL {
User getUserById (Long userId);

List <User> getAllUsers ();

User createUser (newUserRequest request);

User updateUser(Long userId, updateUserRequest request); //por ahora lo dejamos así

void deleteUser(Long userId);

List <User> findUserByRole(Role role);

Optional <User> assingRoleForUser(Long userId,Role role);

Optional <User> removeRoleFromUser(Long userId, Role role);

UserDto convertToDto(User user);
}
