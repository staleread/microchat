package edu.microchat.message;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
class MessageListener {
  private final MessageService messageService;

  public MessageListener(MessageService messageService) {
    this.messageService = messageService;
  }

  @RabbitListener(queues = "${microchat.queues.assistant-replies}")
  public void handleAssistantReply(String reply) {
    messageService.createFromAssistantReply(reply);
  }
}
