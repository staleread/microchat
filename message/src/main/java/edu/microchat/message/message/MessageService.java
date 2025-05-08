package edu.microchat.message.message;

import edu.microchat.message.assistant.AssistantApiClient;
import edu.microchat.message.assistant.AssistantPromptDto;
import edu.microchat.message.assistant.AssistantReplyEvent;
import edu.microchat.message.user.UserApiClient;
import edu.microchat.message.user.UserDto;
import java.util.List;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
class MessageService {
  private final AssistantApiClient assistantApiClient;
  private final UserApiClient userApiClient;
  private final MessageRepository messageRepository;

  public MessageService(
      AssistantApiClient assistantApiClientService,
      UserApiClient userApiClient,
      MessageRepository messageRepository) {
    this.assistantApiClient = assistantApiClientService;
    this.userApiClient = userApiClient;
    this.messageRepository = messageRepository;
  }

  public List<MessageResponse> getAll(int page, int count) {
    var pageable = PageRequest.of(page, count);

    return messageRepository.findAllByOrderByTimestampDesc(pageable).stream()
        .map(MessageService::mapToMessageResponse)
        .toList();
  }

  public long create(MessageCreateRequest request) {
    Message message = mapToMessage(request);

    if (message.isAssistantPrompt()) {
      UserDto user = userApiClient.getUserById(message.getSenderId());
      AssistantPromptDto dto = mapToAssistantPromptDto(user, message);

      assistantApiClient.sendAssistantPrompt(dto);
    }

    return messageRepository.save(message).getId();
  }

  @EventListener
  public void handleAssistantReply(AssistantReplyEvent event) {
    var message = Message.assistantMessage(event.reply());

    messageRepository.save(message).getId();
  }

  private static AssistantPromptDto mapToAssistantPromptDto(UserDto userDto, Message message) {
    var messageSender =
        new AssistantPromptDto.MessageSender(userDto.id(), userDto.username(), userDto.bio());

    return new AssistantPromptDto(messageSender, message.getContent());
  }

  private static Message mapToMessage(MessageCreateRequest request) {
    return new Message(request.senderId(), request.content());
  }

  private static MessageResponse mapToMessageResponse(Message message) {
    return new MessageResponse(
        message.getId(), message.getSenderId(), message.getContent(), message.getTimestamp());
  }
}
