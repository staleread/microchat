package edu.microchat.message;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.Length;

record UserDto(@Positive Long id, @NotBlank String username, @Length(max = 100) String bio) {}
