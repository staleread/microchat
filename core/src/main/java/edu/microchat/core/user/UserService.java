package edu.microchat.core.user;

import edu.microchat.core.common.ApiResponse;
import edu.microchat.core.common.BaseMetadata;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService implements UserDetailsService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return userRepository
        .findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
  }

  public List<UserDto> getAll() {
    return userRepository.findAll().stream().map(UserService::mapToUserDto).toList();
  }

  public UserDto getById(long id) {
    return userRepository
        .findById(id)
        .map(UserService::mapToUserDto)
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
    user.setFirstName(request.firstName());
    user.setLastName(request.lastName());
    user.setDepartment(request.department());
    user.setBio(request.bio());
    userRepository.save(user);
  }

  public void delete(long id) {
    if (!userRepository.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
    }

    userRepository.deleteById(id);
  }

  public ApiResponse<BaseMetadata, UserDto> getAllAsApiResponse() {
    try {
      return new ApiResponse<>(BaseMetadata.success(200), getAll());
    } catch (ResponseStatusException e) {
      return new ApiResponse<>(BaseMetadata.error(e.getStatusCode().value(), e.getReason()));
    }
  }

  public ApiResponse<BaseMetadata, UserDto> getByIdAsApiResponse(long id) {
    try {
      return new ApiResponse<>(BaseMetadata.success(200), getById(id));
    } catch (ResponseStatusException e) {
      return new ApiResponse<>(BaseMetadata.error(e.getStatusCode().value(), e.getReason()));
    }
  }

  public ApiResponse<BaseMetadata, Long> createAsApiResponse(UserCreateRequest request) {
    try {
      return new ApiResponse<>(BaseMetadata.success(200), create(request));
    } catch (ResponseStatusException e) {
      return new ApiResponse<>(BaseMetadata.error(e.getStatusCode().value(), e.getReason()));
    }
  }

  public ApiResponse<BaseMetadata, Void> updateAsApiResponse(long id, UserUpdateRequest request) {
    try {
      update(id, request);
      return new ApiResponse<>(BaseMetadata.success(200));
    } catch (ResponseStatusException e) {
      return new ApiResponse<>(BaseMetadata.error(e.getStatusCode().value(), e.getReason()));
    }
  }

  public ApiResponse<BaseMetadata, Void> deleteAsApiResponse(long id) {
    try {
      delete(id);
      return new ApiResponse<>(BaseMetadata.success(200));
    } catch (ResponseStatusException e) {
      return new ApiResponse<>(BaseMetadata.error(e.getStatusCode().value(), e.getReason()));
    }
  }

  private User mapToUser(UserCreateRequest request) {
    Role role = request.role() != null ? request.role() : Role.STUDENT;
    String encodedPassword = passwordEncoder.encode(request.password());
    return new User(
        request.username(),
        encodedPassword,
        request.firstName(),
        request.lastName(),
        request.department(),
        request.bio(),
        role);
  }

  private static UserDto mapToUserDto(User user) {
    return new UserDto(
        user.getId(),
        user.getUsername(),
        user.getFirstName(),
        user.getLastName(),
        user.getDepartment(),
        user.getBio());
  }
}
