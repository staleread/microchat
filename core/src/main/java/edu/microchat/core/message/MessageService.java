package edu.microchat.core.message;

import edu.microchat.core.assistant.AssistantApiClient;
import edu.microchat.core.assistant.AssistantPromptDto;
import edu.microchat.core.assistant.AssistantReplyEvent;
import edu.microchat.core.common.ApiResponse;
import edu.microchat.core.common.BaseMetadata;
import edu.microchat.core.common.PaginationMetadata;
import edu.microchat.core.common.PaginationRequest;
import edu.microchat.core.user.UserDto;
import edu.microchat.core.user.UserService;
import java.util.List;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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

  public List<MessageDto> getAll() {
    return messageRepository.findAll().stream().map(MessageService::mapToMessageDto).toList();
  }

  public ApiResponse<BaseMetadata, MessageDto> getAllAsApiResponse() {
    try {
      List<MessageDto> messages = getAll();
      if (messages.isEmpty()) {
        return new ApiResponse<>(BaseMetadata.error(404, "No messages found"));
      }
      return new ApiResponse<>(BaseMetadata.success(200), messages);
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

  public ApiResponse<PaginationMetadata, MessageDto> getMessagesPageAsApiResponse(
      PaginationRequest request) {
    if (request.page() < 0) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "Page index must not be less than zero");
    }
    if (request.size() < 1) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Page size must not be less than one");
    }

    var pageable = PageRequest.of(request.page(), request.size());
    var page = messageRepository.findAllByOrderByTimestampDesc(pageable);

    if (request.page() > 0 && request.page() >= page.getTotalPages() && page.getTotalElements() > 0) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Page index out of range");
    }

    var meta =
        PaginationMetadata.builder()
            .code(200)
            .success(true)
            .number(page.getNumber())
            .size(page.getSize())
            .totalElements(page.getTotalElements())
            .totalPages(page.getTotalPages())
            .isFirst(page.isFirst())
            .isLast(page.isLast())
            .build();

    return new ApiResponse<>(
        meta, page.getContent().stream().map(MessageService::mapToMessageDto).toList());
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
