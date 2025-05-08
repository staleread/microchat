package edu.microchat.message.assistant;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class AssistantApiClient {
  private final ApplicationEventPublisher eventPublisher;
  private final String promptsQueueName;
  private final RabbitTemplate rabbitTemplate;

  public AssistantApiClient(
      ApplicationEventPublisher eventPublisher,
      @Value("${microchat.queues.assistant-prompts}") String promptsQueueName,
      RabbitTemplate rabbitTemplate) {
    this.eventPublisher = eventPublisher;
    this.promptsQueueName = promptsQueueName;
    this.rabbitTemplate = rabbitTemplate;
  }

  public void sendAssistantPrompt(AssistantPromptDto dto) {
    rabbitTemplate.convertAndSend(promptsQueueName, dto);
  }

  @RabbitListener(
      queues = "${microchat.queues.assistant-replies}",
      messageConverter = "jsonConverter")
  public void handleAssistantReply(AssistantReplyDto dto) {
    eventPublisher.publishEvent(mapToReplyEvent(dto));
  }

  private static AssistantReplyEvent mapToReplyEvent(AssistantReplyDto dto) {
    return new AssistantReplyEvent(dto.reply());
  }
}
