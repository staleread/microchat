package edu.microchat.message;

import java.util.List;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
class MessageService {
  private final String promptsQueueName;
  private final RabbitTemplate rabbitTemplate;
  private final DiscoveryClient discoveryClient;
  private final RestTemplate restTemplate;
  private final MessageRepository messageRepository;

  public MessageService(
      @Value("${microchat.queues.assistant-prompts}") String promptsQueueName,
      RabbitTemplate rabbitTemplate,
      DiscoveryClient discoveryClient,
      RestTemplate restTemplate,
      MessageRepository messageRepository) {
    this.promptsQueueName = promptsQueueName;
    this.rabbitTemplate = rabbitTemplate;
    this.discoveryClient = discoveryClient;
    this.restTemplate = restTemplate;
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
      sendAssistantPrompt(message);
    }

    return messageRepository.save(message).getId();
  }

  public void createFromAssistantReply(AssistantReplyDto dto) {
    var message = Message.assistantMessage(dto.reply());

    System.out.println("Reply: " + message.getContent());

    messageRepository.save(message).getId();
  }

  private void sendAssistantPrompt(Message message) {
    ServiceInstance userServiceInstance = discoveryClient.getInstances("user").getFirst();
    String userGetEndpoint =
        userServiceInstance.getUri() + "/api/v1/users/" + message.getSenderId();

    // TODO: add user response validation
    UserDto userResponse = restTemplate.getForEntity(userGetEndpoint, UserDto.class).getBody();

    var messageSender =
        new AssistantPromptDto.MessageSender(
            userResponse.id(), userResponse.username(), userResponse.bio());
    var dto = new AssistantPromptDto(messageSender, message.getContent());

    rabbitTemplate.convertAndSend(promptsQueueName, dto);
  }

  private static Message mapToMessage(MessageCreateRequest request) {
    return new Message(request.senderId(), request.content());
  }

  private static MessageResponse mapToMessageResponse(Message message) {
    return new MessageResponse(
        message.getId(), message.getSenderId(), message.getContent(), message.getTimestamp());
  }
}
