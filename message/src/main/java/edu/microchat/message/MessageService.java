package edu.microchat.message;

import java.util.List;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
class MessageService {
  private final String promptsQueueName;
  private final AmqpTemplate amqpTemplate;
  private final MessageRepository messageRepository;

  public MessageService(
      @Value("${microchat.queues.assistant-prompts}") String promptsQueueName,
      AmqpTemplate amqpTemplate,
      MessageRepository messageRepository) {
    this.promptsQueueName = promptsQueueName;
    this.amqpTemplate = amqpTemplate;
    this.messageRepository = messageRepository;
  }

  public List<MessageResponse> getAll(int page, int count) {
    var pageable = PageRequest.of(page, count);

    return messageRepository.findAllByOrderByTimestampDesc(pageable).stream()
        .map(MessageService::mapToMessageResponse)
        .toList();
  }

  public long create(MessageCreateRequest request) {
    var message = mapToMessage(request);

    System.out.println("User message: " + message.getContent());

    if (message.isAssistantPrompt()) {
      // TODO: edit message format
      amqpTemplate.convertAndSend(promptsQueueName, message.getContent());
    }

    return messageRepository.save(message).getId();
  }

  // TODO: redesign the method
  public void createFromAssistantReply(String reply) {
    long ASSISTANT_ID = 1;
    var message = new Message(ASSISTANT_ID, reply);

    System.out.println("Reply: " + message.getContent());

    messageRepository.save(message).getId();
  }

  private static Message mapToMessage(MessageCreateRequest request) {
    return new Message(request.senderId(), request.content());
  }

  private static MessageResponse mapToMessageResponse(Message message) {
    return new MessageResponse(
        message.getId(), message.getSenderId(), message.getContent(), message.getTimestamp());
  }
}
