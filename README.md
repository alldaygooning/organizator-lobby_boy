# LobbyBoy - User Microservice

## Table of Contents
- [Overview](#overview)
- [Building and Deployment](#building-and-deployment)
- [Request Validation](#request-validation)
- [Security Architecture](#security-architecture)
- [Authentication](#authentication)

## Overview

LobbyBoy is a Spring Boot user management microservice for the [Organizator](https://github.com/alldaygooning/organizator) project, handling user registration, authentication, and JWT token management.

[![Java](https://img.shields.io/badge/Java-%23ED8B00.svg?logo=openjdk&logoColor=white)](#)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?logo=springboot&logoColor=fff)](#)
![Security](https://img.shields.io/badge/Spring-Security-red)
![Validation](https://img.shields.io/badge/Jakarta-Validation-purple)

[![Postgres](https://img.shields.io/badge/Postgres-%23316192.svg?logo=postgresql&logoColor=white)](#)
![JWT](https://img.shields.io/badge/JWT-0.12.6-orange)
[![Docker](https://img.shields.io/badge/Docker-2496ED?logo=docker&logoColor=fff)](#)

## Building and Deployment

The project supports two deployment modes:

**Development Mode**: Runs in [Docker container](https://github.com/alldaygooning/organizator-lobby_boy/blob/master/Dockerfile.dev) as part of [Docker Compose network](https://github.com/alldaygooning/organizator/blob/master/docker-compose.dev.yaml) with [HotSwapAgent](https://github.com/HotswapProjects/HotswapAgent) for runtime class reloading and JDWP debugging support on port 5005. For this configuration to work, you will need **my custom [hotswapagent-runtime](https://hub.docker.com/r/debi1/hotswapagent-runtime/tags) base image**.

**Production Mode**:  Runs in [Docker container](https://github.com/alldaygooning/organizator-lobby_boy/blob/master/Dockerfile.prod) as part of [Docker Compose network](https://github.com/alldaygooning/organizator/blob/master/docker-compose.prod.yaml) utilizing a standard Eclipse Temurin JDK 17 base image.

For comprehensive deployment instructions and environment configuration details, refer to the [📘 Organizator Deployment Guide](https://github.com/alldaygooning/organizator?tab=readme-ov-file#development-mode).

## Request Validation

All incoming requests are validated using Jakarta Bean Validation constraints. The validation framework ensures data integrity by enforcing constraints on request payloads, including field presence checks, length boundaries, and format requirements. Validation errors return structured responses with appropriate error codes.

## Security Architecture

The service implements a dual-filter chain security model through Spring Security configuration:

**Public Chain**  
Handles user-facing endpoints under `/api/users/**` with permit-all access for registration and authentication operations.

**Secure Chain**  
Protects internal microservice endpoints under `/api/jwt/**` requiring `ROLE_MICROSERVICE` authority. This chain incorporates an API filter that validates requests using a shared API key header, establishing microservice-to-microservice trust.

## Authentication

Authentication is implemented using JSON Web Tokens (JWT) issued as HTTP-only cookies. The token-based authentication flow includes:

- User credentials validation against securely hashed passwords
- JWT token generation containing user identity claims
- Stateless session management through token validation
- Secure token storage in HTTP-only cookies with configurable expiration
- Programmatic token validation (via `X-API-KEY` HTTP header) for inter-service communication

The system employs secure password hashing with salt and pepper techniques to protect user credentials.