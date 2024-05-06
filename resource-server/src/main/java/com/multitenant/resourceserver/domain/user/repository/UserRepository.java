package com.multitenant.resourceserver.domain.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.multitenant.resourceserver.domain.user.model.User;
import java.util.List;



@Repository
public interface UserRepository extends JpaRepository<User, String> {
 
    Optional<User> findByKeycloakId(String keycloakId);

    List<User> findByUsername(String username);

}
