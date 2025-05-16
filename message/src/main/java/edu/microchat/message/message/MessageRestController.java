package edu.microchat.message.message;

import edu.microchat.message.MessageAppConfig;
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
  private final MessageAppConfig appConfig;

  public MessageRestController(MessageService messageService, MessageAppConfig appConfig) {
    this.messageService = messageService;
    this.appConfig = appConfig;
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

  @GetMapping("/moto")
  public String getProjectMoto() {
    return appConfig.projectMoto();
  }
}
