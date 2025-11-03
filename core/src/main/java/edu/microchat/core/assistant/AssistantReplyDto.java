package edu.microchat.core.assistant;

import jakarta.validation.constraints.NotBlank;

public record AssistantReplyDto(@NotBlank String reply) {}
