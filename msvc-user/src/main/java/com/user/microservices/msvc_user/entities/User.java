package com.user.microservices.msvc_user.entities;


import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "users")
public class User {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

@NotBlank
@Size(max = 30, message = "The name cannot have more than 30 characters") 
@Column(name =  "first_name")
private String firstname;

@NotBlank
@Size(max = 30, message = "the lastname cannot have more than 30 characters")
@Column(name =  "last_name")
private String lastname;

@NotBlank
@Email
@Column(unique = true, nullable = false)
private String email;

@Column(name = "phone_number")
private String phoneNumber;

@NotBlank(message = "password is mandatory")
@Column(nullable = false)
private String password;

@ElementCollection(fetch = FetchType.EAGER)
@CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
@Column(name = "role", nullable = false)
@Enumerated(EnumType.STRING)
private Set <Role> roles = new HashSet<>();  //evitamos repetidos con el set

//con esto vamos a poder habilitar y desabilitar usuarios segun se necesite (SOLO ADMIN)
@Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
private boolean enabled = true;

public void addRole(Role role){
    this.roles.add(role);
}

public void removeRole(Role role){
    this.roles.remove(role);
}

}
