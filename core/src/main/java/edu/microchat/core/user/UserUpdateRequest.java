package edu.microchat.core.user;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

record UserUpdateRequest(@NotBlank String username, @Length(min = 4, max = 100) String bio) {}
