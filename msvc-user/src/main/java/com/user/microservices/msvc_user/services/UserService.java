package com.user.microservices.msvc_user.services;

import java.util.List;
import java.util.Optional;


import org.springframework.stereotype.Service;

import com.user.microservices.msvc_user.entities.Role;
import com.user.microservices.msvc_user.entities.User;
import com.user.microservices.msvc_user.exceptions.ResourceAlreadyExistExcp;
import com.user.microservices.msvc_user.exceptions.ResourceNotFoundException;
import com.user.microservices.msvc_user.repositories.UserRepository;
import com.user.microservices.msvc_user.request.newUserRequest;
import com.user.microservices.msvc_user.request.updateUserRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService implements UserServiceIMPL {
   private final UserRepository userRepository;

    @Override
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
        .orElseThrow( () -> new ResourceNotFoundException("User not found, please try again"));
    }

    @Override
    public List<User> getAllUsers() {
       return (List<User>) userRepository.findAll();
    }

   @Override
    public User createUser(newUserRequest request) {
        return Optional.of(request).filter(user -> !userRepository.existByEmail(request.getEmail()))
        .map(req -> {
            User user = new User();
            user.setFirstname(request.getFirstname());
            user.setLastname(request.getLastname());
            user.setEmail(request.getEmail());
            user.setPhoneNumber(request.getPhoneNumber());
            user.setPassword(request.getPassword()); //recordar que esta contraseña debemos pasarla encodeada (HACER MAS TARDE!)
            return userRepository.save(user);
        }).orElseThrow( () -> new ResourceAlreadyExistExcp("User already exist!"));
    }

    @Override
    public User updateUser(Long userId, updateUserRequest request) {
        return userRepository.findById(userId).map(existingUser -> {
            existingUser.setFirstname(request.getFirstname());
            existingUser.setLastname(request.getLastname());
            // Add other fields as needed
            return userRepository.save(existingUser);
        }).orElseThrow(() -> new ResourceNotFoundException("User not found, please try again"));
    }

    @Override
    public void deleteUser(Long userId) {
       userRepository.findById(userId).ifPresentOrElse(userRepository :: delete , () -> new ResourceNotFoundException("User not found. cant delete, please try again"));
    }

    @Override
    public List<User> findUserByRole(Role role) {
       return (List<User>) userRepository.findByRole(role).orElseThrow(() -> new ResourceNotFoundException("cant find this user by role, please try again"));
    }

    @Override
    public Optional<User> assingRoleForUser(Long userId, Role role) {
        return userRepository.findById(userId).map(user ->{
            user.addRole(role);
            return userRepository.save(user);
        }
        );
    }

    @Override
    public Optional<User> removeRoleFromUser(Long userId, Role role) {
      return userRepository.findById(userId).map(user ->{
        user.removeRole(role);
        return userRepository.save(user);
      });
    }

}

//NOTA: quizás mas adelante podemos agregar algun método con el que se pueda modificar la contraseña!