package com.multitenant.resourceserver.configuration;

import java.net.URI;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.util.StringUtils;

import com.c4_soft.springaddons.security.oidc.starter.OpenidProviderPropertiesResolver;
import com.c4_soft.springaddons.security.oidc.starter.properties.OpenidProviderProperties;
import com.c4_soft.springaddons.security.oidc.starter.properties.SpringAddonsOidcProperties;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class MultitenantSecurityConfig implements OpenidProviderPropertiesResolver {

    @Autowired
    private SpringAddonsOidcProperties properties;

    @Override
    public Optional<OpenidProviderProperties> resolve(Map<String, Object> claimSet) {
        final String tokenIss = Optional
            .ofNullable(claimSet.get(JwtClaimNames.ISS))
            .map(Object::toString)
            .orElseThrow(() -> new RuntimeException("Invalid token: missing issuer"));
        return properties.getOps().stream().filter(opProps -> {
            final String opBaseHref = Optional.ofNullable(opProps.getIss()).map(URI::toString).orElse(null);
            if (!StringUtils.hasText(opBaseHref)) {
                return false;
            }
            return tokenIss.startsWith(opBaseHref);
        }).findAny();
    }

}
