package edu.microchat.core.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.util.List;
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
  public List<UserResponse> getAll() {
    return userService.getAll();
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public UserResponse getById(@Positive @PathVariable long id) {
    return userService.getById(id);
  }

  @PostMapping("/")
  @PreAuthorize("hasRole('ADMIN')")
  public Long create(@Valid @RequestBody UserCreateRequest request) {
    return userService.create(request);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public void update(
      @Positive @PathVariable long id, @Valid @RequestBody UserUpdateRequest request) {
    userService.update(id, request);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public void delete(@Positive @PathVariable long id) {
    userService.delete(id);
  }
}
