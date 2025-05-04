package edu.microchat.assistant;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
class AssistantListener {
  private final AssistantService assistantService;

  public AssistantListener(AssistantService assistantService) {
    this.assistantService = assistantService;
  }

  @RabbitListener(queues = "${microchat.queues.assistant-prompts}")
  public void handleUserPrompt(String prompt) {
    assistantService.processPrompt(prompt);
  }
}
