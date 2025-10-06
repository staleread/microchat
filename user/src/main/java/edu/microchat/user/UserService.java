package edu.microchat.user;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
class UserService {
  private final UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public List<UserResponse> getAll() {
    return userRepository.findAll().stream().map(UserService::mapToUserResponse).toList();
  }

  public UserResponse getById(long id) {
    return userRepository
        .findById(id)
        .map(UserService::mapToUserResponse)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
  }

  public Long create(UserCreateRequest request) {
    if (userRepository.existsByUsername(request.username())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username is already taken");
    }

    User user = mapToUser(request);
    return userRepository.save(user).getId();
  }

  public void update(long id, UserUpdateRequest request) {
    User user =
        userRepository
            .findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

    if (!user.getUsername().equals(request.username())
        && userRepository.existsByUsername(request.username())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username is already taken");
    }

    user.setUsername(request.username());
    user.setBio(request.bio());
    userRepository.save(user);
  }

  public void delete(long id) {
    if (!userRepository.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
    }

    userRepository.deleteById(id);
  }

  private static User mapToUser(UserCreateRequest request) {
    return new User(request.username(), request.bio());
  }

  private static UserResponse mapToUserResponse(User user) {
    return new UserResponse(user.getId(), user.getUsername(), user.getBio());
  }
}
