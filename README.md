# Multitenant Application Approach with Spring Cloud Gateway, Keycloak, and Spring OAuth2 Resource Server

  

This project demonstrates how to implement a multitenant application architecture using Spring Cloud Gateway for routing, Keycloak for authentication, and Spring OAuth2 Resource Server for securing resources.

  

## Prerequisites

  

- Docker installed on your machine to run the Keycloak instance.

  

## Keycloak Setup

  

To run Keycloak with Docker, execute the following command:

  

```bash

docker  run  -p  8080:8080  -e  KEYCLOAK_ADMIN=admin  -e  KEYCLOAK_ADMIN_PASSWORD=admin  quay.io/keycloak/keycloak:22.0.5  start-dev

```

  

## Postman Template Reference

  

For testing the endpoints of your application, you can use the provided Postman template. Import the template into your Postman application to get started quickly.

  

[Download postmant collection](./spring-keycloack-multitenant.postman_collection.json)
