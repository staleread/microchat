package edu.microchat.user;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

record UserCreateRequest(@NotBlank String username, @Length(min = 4) String bio) {}
