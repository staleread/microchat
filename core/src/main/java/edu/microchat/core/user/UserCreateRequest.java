package edu.microchat.core.user;

import jakarta.validation.constraints.NotBlank;
import java.util.Set;
import org.hibernate.validator.constraints.Length;

record UserCreateRequest(
    @NotBlank String username,
    @NotBlank String password,
    @NotBlank String firstName,
    @NotBlank String lastName,
    String department,
    @Length(min = 4, max = 100) String bio,
    Set<Role> roles) {}
