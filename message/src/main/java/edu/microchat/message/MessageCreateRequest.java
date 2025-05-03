package edu.microchat.message;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

record MessageCreateRequest(@Positive long senderId, @NotBlank String content) {}
