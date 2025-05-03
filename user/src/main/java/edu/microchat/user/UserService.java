package edu.microchat.user;

import java.util.List;
import org.springframework.stereotype.Service;

@Service
class UserService {
  private final UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public List<UserResponse> getAll() {
    return userRepository.findAll().stream().map(UserService::mapToUserResponse).toList();
  }

  public Long create(UserCreateRequest request) {
    User user = mapToUser(request);
    return userRepository.save(user).getId();
  }

  private static User mapToUser(UserCreateRequest request) {
    return new User(request.username());
  }

  private static UserResponse mapToUserResponse(User user) {
    return new UserResponse(user.getId(), user.getUsername(), user.getBio());
  }
}
