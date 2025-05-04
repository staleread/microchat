package edu.microchat.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
  public List<UserResponse> getAll() {
    return userService.getAll();
  }

  @GetMapping("/:id")
  public UserResponse getById(@Positive @PathVariable long id) {
    return userService.getById(id);
  }

  @PostMapping("/")
  public Long create(@Valid @RequestBody UserCreateRequest request) {
    return userService.create(request);
  }
}
