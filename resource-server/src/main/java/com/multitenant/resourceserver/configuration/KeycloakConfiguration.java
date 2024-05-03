package com.multitenant.resourceserver.configuration;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakConfiguration {
    
    @Bean
    Keycloak keycloak() {
        return KeycloakBuilder.builder()
            .serverUrl("http://localhost:8080")
            .realm("master")
            .clientId("admin-cli")
            .grantType("password")
            .username("admin")
            .password("admin")
            .build();
    }

}
