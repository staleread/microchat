package edu.microchat.message;

import java.io.Serializable;

record AssistantPromptDto(MessageSender sender, String prompt) implements Serializable {
  record MessageSender(Long id, String username, String bio) implements Serializable {}
}
