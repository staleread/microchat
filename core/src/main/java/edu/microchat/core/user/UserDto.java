package edu.microchat.core.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.Length;

public record UserDto(
    @Positive Long id, @NotBlank String username, @Length(max = 100) String bio) {}
