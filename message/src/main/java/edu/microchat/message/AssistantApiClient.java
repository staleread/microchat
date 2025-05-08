package edu.microchat.message;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
class AssistantApiClient {
  private final String promptsQueueName;
  private final RabbitTemplate rabbitTemplate;

  public AssistantApiClient(
      @Value("${microchat.queues.assistant-prompts}") String promptsQueueName,
      RabbitTemplate rabbitTemplate) {
    this.promptsQueueName = promptsQueueName;
    this.rabbitTemplate = rabbitTemplate;
  }

  public void sendAssistantPrompt(AssistantPromptDto dto) {
    rabbitTemplate.convertAndSend(promptsQueueName, dto);
  }
}
