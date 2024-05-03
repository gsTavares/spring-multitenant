package com.multitenant.resourceserver.domain.user.resource;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.multitenant.resourceserver.domain.user.model.CreateUserRequest;

@RestController
@RequestMapping("user")
public class UserController {

    @Autowired
    private Keycloak keycloak;

    @PostMapping
    ResponseEntity<Object> create(@RequestBody CreateUserRequest createUserRequest, Authentication authentication) {

        Map<String, Object> response = new HashMap<>();

        String realm = getRealm(authentication);

        UserRepresentation userRepresentation = new UserRepresentation();
        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();

        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
        credentialRepresentation.setTemporary(false);
        credentialRepresentation.setValue(createUserRequest.password());

        userRepresentation.setUsername(createUserRequest.username());
        userRepresentation.setCredentials(Arrays.asList(credentialRepresentation));
        userRepresentation.setEnabled(true);

        userRepresentation
                .setRealmRoles(authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList());

        keycloak.realm(realm).users().create(userRepresentation);

        response.put("message", "created!");

        return ResponseEntity.ok(response);

    }

    @GetMapping
    ResponseEntity<Object> listByRealm(@RequestParam(required = false, defaultValue = "") String username,
            Authentication authentication) {

        String realm = getRealm(authentication);

        List<UserRepresentation> users = keycloak.realm(realm).users().search(username);

        return ResponseEntity.ok(users);

    }

    @GetMapping("{id}")
    ResponseEntity<Object> listById(@PathVariable String id, Authentication authentication) {

        String realm = getRealm(authentication);

        UserRepresentation user = keycloak.realm(realm).users().get(id).toRepresentation();

        return ResponseEntity.ok(user);

    }

    @PutMapping("{id}")
    ResponseEntity<Object> update(@PathVariable String id, @RequestBody CreateUserRequest createUserRequest,
            Authentication authentication) {

        Map<String, Object> response = new HashMap<>();

        String realm = getRealm(authentication);

        UserResource userResource = keycloak.realm(realm).users().get(id);

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

        System.out.println(toEdit.getUsername());

        userResource.update(toEdit);

        response.put("message", id + " updated!");

        return ResponseEntity.ok(response);

    }

    @PutMapping("/change-status/{id}")
    ResponseEntity<Object> changeStatus(@PathVariable String id, Authentication authentication) {

        Map<String, Object> response = new HashMap<>();

        String realm = getRealm(authentication);

        UserResource userResource = keycloak.realm(realm).users().get(id);
        UserRepresentation toEdit = userResource.toRepresentation();

        toEdit.setEnabled(!toEdit.isEnabled());

        userResource.update(toEdit);

        response.put("message", id + (toEdit.isEnabled() ? " enabled!" : " disabled!"));

        return ResponseEntity.ok(response);

    }

    private String getRealm(Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String realm = jwt.getClaimAsString("iss").split("realms/")[1];
        return realm;
    }

}
