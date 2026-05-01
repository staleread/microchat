package edu.microchat.core.message;

import edu.microchat.core.CoreAppConfig;
import edu.microchat.core.common.ApiResponse;
import edu.microchat.core.common.BaseMetadata;
import edu.microchat.core.common.PaginationMetadata;
import edu.microchat.core.common.PaginationRequest;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/messages")
class MessageRestController {
  private final MessageService messageService;
  private final CoreAppConfig appConfig;

  public MessageRestController(MessageService messageService, CoreAppConfig appConfig) {
    this.messageService = messageService;
    this.appConfig = appConfig;
  }

  @GetMapping("/")
  @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR')")
  public ApiResponse<PaginationMetadata, MessageDto> getAll(@Valid PaginationRequest request) {
    return messageService.getMessagesPageAsApiResponse(request);
  }

  @GetMapping("/all")
  @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR')")
  public ApiResponse<BaseMetadata, MessageDto> getAll() {
    return messageService.getAllAsApiResponse();
  }

  @PostMapping("/")
  @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR')")
  public ApiResponse<BaseMetadata, Long> create(@Valid @RequestBody MessageCreateRequest request) {
    return messageService.createAsApiResponse(request);
  }

  @GetMapping("/moto")
  public String getUniversityMoto() {
    return appConfig.universityMoto();
  }
}
