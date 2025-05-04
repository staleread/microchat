package edu.microchat.assistant;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
class AssistantService {
  private final String repliesQueueName;
  private final AmqpTemplate amqpTemplate;

  public AssistantService(
      @Value("${microchat.queues.assistant-replies}") String repliesQueueName,
      AmqpTemplate amqpTemplate) {
    this.repliesQueueName = repliesQueueName;
    this.amqpTemplate = amqpTemplate;
  }

  public void processPrompt(String prompt) {
    var reply = "Assistant: " + prompt;
    amqpTemplate.convertAndSend(repliesQueueName, reply);
  }
}
