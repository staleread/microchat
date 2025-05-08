package edu.microchat.message;

import java.util.List;
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

  public void createFromAssistantReply(AssistantReplyDto dto) {
    var message = Message.assistantMessage(dto.reply());

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
