package edu.microchat.core.user;

import edu.microchat.core.common.ApiResponse;
import edu.microchat.core.common.BaseMetadata;
import edu.microchat.core.common.PaginationMetadata;
import edu.microchat.core.common.PaginationRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/users")
class UserRestController {
  private final UserService userService;

  public UserRestController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/")
  @PreAuthorize("hasRole('ADMIN')")
  public ApiResponse<PaginationMetadata, UserDto> getAll(@Valid PaginationRequest request) {
    return userService.getUsersPageAsApiResponse(request);
  }

  @GetMapping("/all")
  @PreAuthorize("hasRole('ADMIN')")
  public ApiResponse<BaseMetadata, UserDto> getAll() {
    return userService.getAllAsApiResponse();
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ApiResponse<BaseMetadata, UserDto> getById(@Positive @PathVariable long id) {
    return userService.getByIdAsApiResponse(id);
  }

  @PostMapping("/")
  @PreAuthorize("hasRole('ADMIN')")
  public ApiResponse<BaseMetadata, Long> create(@Valid @RequestBody UserCreateRequest request) {
    return userService.createAsApiResponse(request);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ApiResponse<BaseMetadata, Void> update(
      @Positive @PathVariable long id, @Valid @RequestBody UserUpdateRequest request) {
    return userService.updateAsApiResponse(id, request);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ApiResponse<BaseMetadata, Void> delete(@Positive @PathVariable long id) {
    return userService.deleteAsApiResponse(id);
  }
}
