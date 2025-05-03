package edu.microchat.message;

import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
class MessageService {
  private final MessageRepository messageRepository;

  public MessageService(MessageRepository messageRepository) {
    this.messageRepository = messageRepository;
  }

  public List<MessageResponse> getAll(int page, int count) {
    var pageable = PageRequest.of(page, count);

    return messageRepository.findAllByOrderByTimestampDesc(pageable).stream()
        .map(MessageService::mapToMessageResponse)
        .toList();
  }

  public List<MessageResponse> getUnprocessedBySenderId(long senderId) {
    return messageRepository.findBySenderIdAndIsProcessedFalse(senderId).stream()
        .map(MessageService::mapToMessageResponse)
        .toList();
  }

  public long create(MessageCreateRequest request) {
    var message = mapToMessage(request);
    return messageRepository.save(message).getId();
  }

  private static Message mapToMessage(MessageCreateRequest request) {
    return new Message(request.senderId(), request.content());
  }

  private static MessageResponse mapToMessageResponse(Message message) {
    return new MessageResponse(
        message.getId(), message.getSenderId(), message.getContent(), message.getTimestamp());
  }
}
