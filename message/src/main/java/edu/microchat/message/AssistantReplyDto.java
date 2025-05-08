package edu.microchat.message;

import jakarta.validation.constraints.NotBlank;

record AssistantReplyDto(@NotBlank String reply) {}
