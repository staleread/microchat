package edu.microchat.core.security;

import jakarta.validation.constraints.NotBlank;

record AuthenticationRequest(@NotBlank String username, @NotBlank String password) {}
