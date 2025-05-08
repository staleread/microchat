package edu.microchat.assistant;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
class AssistantService {
  private final String repliesQueueName;
  private final RabbitTemplate template;

  public AssistantService(
      @Value("${microchat.queues.assistant-replies}") String repliesQueueName,
      RabbitTemplate template) {
    this.repliesQueueName = repliesQueueName;
    this.template = template;
  }

  public void processPrompt(AssistantPromptDto dto) {
    var reply = new AssistantReplyDto("Assistant: " + dto.prompt());
    template.convertAndSend(repliesQueueName, reply);
  }
}
