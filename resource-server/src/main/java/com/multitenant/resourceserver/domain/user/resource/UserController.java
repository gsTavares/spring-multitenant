package com.multitenant.resourceserver.domain.user.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.multitenant.resourceserver.domain.user.model.CreateUserRequest;
import com.multitenant.resourceserver.domain.user.service.KeycloakService;

@RestController
@RequestMapping("user")
public class UserController {

    @Autowired
    private KeycloakService keycloakService;

    @PostMapping
    ResponseEntity<Object> createUser(@RequestBody CreateUserRequest createUserRequest) {

        return keycloakService.createUser(createUserRequest);

    }

    @GetMapping
    ResponseEntity<Object> listUsersByRealm(@RequestParam(required = false, defaultValue = "") String username) {

        return keycloakService.listUsersByRealm(username);

    }

    @GetMapping("{id}")
    ResponseEntity<Object> getUserById(@PathVariable String id) {

        return keycloakService.getUserById(id);

    }

    @PutMapping("{id}")
    ResponseEntity<Object> editUser(@PathVariable String id, @RequestBody CreateUserRequest createUserRequest) {

        return keycloakService.editUser(id, createUserRequest);

    }

    @PutMapping("/change-status/{id}")
    ResponseEntity<Object> changeStatus(@PathVariable String id) {

        return keycloakService.changeUserStatus(id);

    }

}
