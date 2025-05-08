package edu.microchat.assistant;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.Length;

record AssistantPromptDto(@Valid MessageSender sender, @NotBlank String prompt) {
  record MessageSender(
      @Positive Long id, @NotBlank String username, @Length(max = 100) String bio) {}
}
