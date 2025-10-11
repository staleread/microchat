package edu.microchat.assistant;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
class AssistantListener {
  private final AssistantService assistantService;

  public AssistantListener(AssistantService assistantService) {
    this.assistantService = assistantService;
  }

  @RabbitListener(
      queues = "${microchat.queues.assistant-prompts}",
      messageConverter = "jsonConverter")
  public void handleUserPrompt(AssistantPromptDto dto) {
    assistantService.processPromptMock(dto);
  }
}
