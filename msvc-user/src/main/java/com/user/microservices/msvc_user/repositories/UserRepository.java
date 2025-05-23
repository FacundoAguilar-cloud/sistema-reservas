package com.user.microservices.msvc_user.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.user.microservices.msvc_user.entities.Role;
import com.user.microservices.msvc_user.entities.User;
@Repository 
public interface UserRepository extends CrudRepository<User, Long> {

    Optional <User> findByEmail(String email);

    boolean existByEmail (String email);

    Optional <User> findByRole(Role role);
}
