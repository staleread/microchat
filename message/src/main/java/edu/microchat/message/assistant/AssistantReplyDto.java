package edu.microchat.message.assistant;

import jakarta.validation.constraints.NotBlank;

public record AssistantReplyDto(@NotBlank String reply) {}
