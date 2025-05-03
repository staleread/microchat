package edu.microchat.user;

import jakarta.validation.constraints.NotBlank;

record UserCreateRequest(@NotBlank String username) {}
