package edu.microchat.core.user;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

record UserCreateRequest(
    @NotBlank String username,
    @NotBlank String password,
    @NotBlank String firstName,
    @NotBlank String lastName,
    String department,
    @Length(min = 4, max = 100) String bio,
    Role role) {}
