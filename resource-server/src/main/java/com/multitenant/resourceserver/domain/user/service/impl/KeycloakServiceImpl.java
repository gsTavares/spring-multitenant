package com.multitenant.resourceserver.domain.user.service.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.multitenant.resourceserver.context.TenantContext;
import com.multitenant.resourceserver.domain.user.model.CreateUserRequest;
import com.multitenant.resourceserver.domain.user.model.User;
import com.multitenant.resourceserver.domain.user.repository.UserRepository;
import com.multitenant.resourceserver.domain.user.service.KeycloakService;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status.Family;

@Service
public class KeycloakServiceImpl implements KeycloakService {

    @Autowired
    private Keycloak keycloak;

    @Autowired
    private UserRepository userRepository;

    private final Supplier<NoSuchElementException> userNotFoundError = () -> new NoSuchElementException("user not found!");

    @Override
    public ResponseEntity<Object> createUser(CreateUserRequest createUserRequest) {

        Map<String, Object> response = new HashMap<>();

        String realm = TenantContext.getCurrentTenant();

        UserRepresentation userRepresentation = new UserRepresentation();
        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();

        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
        credentialRepresentation.setTemporary(false);
        credentialRepresentation.setValue(createUserRequest.password());

        userRepresentation.setUsername(createUserRequest.username().toLowerCase());
        userRepresentation.setCredentials(Arrays.asList(credentialRepresentation));
        userRepresentation.setEnabled(true);

        try {

            Response keycloakResponse = keycloak.realm(realm).users().create(userRepresentation);

            if (keycloakResponse.getStatusInfo().getFamily().equals(Family.SUCCESSFUL)) {

                User user = new User();

                String keycloakUserId = keycloak.realm(realm).users().search(createUserRequest.username())
                        .stream().findFirst().map(UserRepresentation::getId)
                        .orElseThrow(userNotFoundError);

                user.setUsername(createUserRequest.username().toLowerCase());
                user.setKeycloakId(keycloakUserId);

                userRepository.saveAndFlush(user);

                response.put("message", "user created successfully!");

                return ResponseEntity.ok(response);

            } else {

                response.put("message", "error while creating user!");

                return ResponseEntity.status(keycloakResponse.getStatus()).body(response);

            }

        } catch (Exception e) {

            e.printStackTrace();

            response.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            
        }

    }

    @Override
    public ResponseEntity<Object> editUser(String userId, CreateUserRequest createUserRequest) {

        Map<String, Object> response = new HashMap<>();

        String realm = TenantContext.getCurrentTenant();

        UserResource userResource = keycloak.realm(realm).users().get(userId);

        UserRepresentation toEdit = userResource.toRepresentation();

        toEdit.setUsername(createUserRequest.username());
        CredentialRepresentation passwordCredential = userResource.credentials().stream()
                .filter(c -> c.getType().equals(CredentialRepresentation.PASSWORD))
                .findFirst().orElse(new CredentialRepresentation());

        if (passwordCredential.getValue() != null) {
            passwordCredential.setValue(createUserRequest.password());
        } else {
            passwordCredential.setType(CredentialRepresentation.PASSWORD);
            passwordCredential.setValue(createUserRequest.password());
        }

        try {

            userResource.update(toEdit);

            User user = userRepository.findByKeycloakId(toEdit.getId()).orElseThrow(userNotFoundError);
            user.setUsername(createUserRequest.username());
            userRepository.saveAndFlush(user);

            response.put("message", "user updated successfully!");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            
            response.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);

        }
        
    }

    @Override
    public ResponseEntity<Object> listUsersByRealm(String usernameToSearch) {

        try {

            List<User> users;

            if(usernameToSearch.isEmpty()) {
                users = userRepository.findAll();
            } else {
                users = userRepository.findByUsername(usernameToSearch.toLowerCase());
            }

            return ResponseEntity.ok(users);

        } catch (Exception e) {

            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);

        }

    }

    @Override
    public ResponseEntity<Object> getUserById(String userId) {

        try {
            
            User user = userRepository.findByKeycloakId(userId).orElseThrow(userNotFoundError);

            return ResponseEntity.ok(user);

        } catch (Exception e) {
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);

        }

    }

    @Override
    public ResponseEntity<Object> changeUserStatus(String userId) {

        Map<String, Object> response = new HashMap<>();

        try {
            
            String realm = TenantContext.getCurrentTenant();
    
            UserResource userResource = keycloak.realm(realm).users().get(userId);
            UserRepresentation toEdit = userResource.toRepresentation();
    
            toEdit.setEnabled(!toEdit.isEnabled());

            userResource.update(toEdit);

            User user = userRepository.findByKeycloakId(userId).orElseThrow(userNotFoundError);
            user.setStatus(!user.getStatus());
            userRepository.saveAndFlush(user);

            response.put("message", "user updated successfully!");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            
            response.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);

        }


    }

}
