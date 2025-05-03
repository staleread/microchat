package edu.microchat.message;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/messages")
class MessageRestController {
  private final MessageService messageService;

  public MessageRestController(MessageService messageService) {
    this.messageService = messageService;
  }

  @GetMapping("/unprocessed")
  public List<MessageResponse> getUnprocessedBySenderId(
      @Positive @RequestParam("senderId") long senderId) {
    return messageService.getUnprocessedBySenderId(senderId);
  }

  @GetMapping("/")
  public List<MessageResponse> getAll(
      @PositiveOrZero @RequestParam("page") int page, @Positive @RequestParam("count") int count) {
    return messageService.getAll(page, count);
  }

  @PostMapping("/")
  public long create(@Valid @RequestBody MessageCreateRequest request) {
    return messageService.create(request);
  }
}
