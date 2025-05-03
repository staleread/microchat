package edu.microchat.user;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.*;

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

  @PostMapping("/")
  public Long create(@Valid @RequestBody UserCreateRequest request) {
    return userService.create(request);
  }
}
