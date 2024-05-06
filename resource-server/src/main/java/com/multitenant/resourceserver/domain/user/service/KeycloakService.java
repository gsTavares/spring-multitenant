package com.multitenant.resourceserver.domain.user.service;

import org.springframework.http.ResponseEntity;

import com.multitenant.resourceserver.domain.user.model.CreateUserRequest;

public interface KeycloakService {
    
    ResponseEntity<Object> createUser(CreateUserRequest createUserRequest);

    ResponseEntity<Object> editUser(String userId, CreateUserRequest createUserRequest);

    ResponseEntity<Object> listUsersByRealm(String usernameToSearch);

    ResponseEntity<Object> getUserById(String userId);
    
    ResponseEntity<Object> changeUserStatus(String userId);

}
