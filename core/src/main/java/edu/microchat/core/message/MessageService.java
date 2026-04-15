package edu.microchat.core.message;

import edu.microchat.core.assistant.AssistantApiClient;
import edu.microchat.core.assistant.AssistantPromptDto;
import edu.microchat.core.assistant.AssistantReplyEvent;
import edu.microchat.core.common.ApiResponse;
import edu.microchat.core.common.BaseMetadata;
import edu.microchat.core.user.UserDto;
import edu.microchat.core.user.UserService;
import java.util.List;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
class MessageService {
  private final AssistantApiClient assistantApiClient;
  private final UserService userService;
  private final MessageRepository messageRepository;

  public MessageService(
      AssistantApiClient assistantApiClientService,
      UserService userService,
      MessageRepository messageRepository) {
    this.assistantApiClient = assistantApiClientService;
    this.userService = userService;
    this.messageRepository = messageRepository;
  }

  public List<MessageDto> getAll(int page, int count) {
    var pageable = PageRequest.of(page, count);

    return messageRepository.findAllByOrderByTimestampDesc(pageable).stream()
        .map(MessageService::mapToMessageDto)
        .toList();
  }

  public long create(MessageCreateRequest request) {
    Message message = mapToMessage(request);

    UserDto user = userService.getById(message.getSenderId());

    if (message.isAssistantPrompt()) {
      AssistantPromptDto dto = mapToAssistantPromptDto(user, message);
      assistantApiClient.sendAssistantPrompt(dto);
    }

    return messageRepository.save(message).getId();
  }

  @EventListener
  public void handleAssistantReply(AssistantReplyEvent event) {
    var message = Message.createAssistantMessage(event.reply());

    messageRepository.save(message).getId();
  }

  public ApiResponse<BaseMetadata, MessageDto> getAllAsApiResponse(int page, int count) {
    try {
      return new ApiResponse<>(BaseMetadata.success(200), getAll(page, count));
    } catch (ResponseStatusException e) {
      return new ApiResponse<>(BaseMetadata.error(e.getStatusCode().value(), e.getReason()));
    }
  }

  public ApiResponse<BaseMetadata, Long> createAsApiResponse(MessageCreateRequest request) {
    try {
      return new ApiResponse<>(BaseMetadata.success(200), create(request));
    } catch (ResponseStatusException e) {
      return new ApiResponse<>(BaseMetadata.error(e.getStatusCode().value(), e.getReason()));
    }
  }

  private static AssistantPromptDto mapToAssistantPromptDto(UserDto userDto, Message message) {
    var messageSender =
        new AssistantPromptDto.MessageSender(userDto.id(), userDto.username(), userDto.bio());

    return new AssistantPromptDto(messageSender, message.getContent());
  }

  private static Message mapToMessage(MessageCreateRequest request) {
    return new Message(request.senderId(), request.content());
  }

  private static MessageDto mapToMessageDto(Message message) {
    return new MessageDto(
        message.getId(),
        message.getSenderId(),
        message.getContent(),
        message.getTimestamp(),
        message.getSource());
  }
}
