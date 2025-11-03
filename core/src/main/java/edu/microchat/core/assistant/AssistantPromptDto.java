package edu.microchat.core.assistant;

import java.io.Serializable;

public record AssistantPromptDto(MessageSender sender, String prompt) implements Serializable {
  public record MessageSender(Long id, String username, String bio) implements Serializable {}
}
