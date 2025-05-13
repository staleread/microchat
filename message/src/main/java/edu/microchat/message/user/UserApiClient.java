package edu.microchat.message.user;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserApiClient {
  private final DiscoveryClient discoveryClient;
  private final RestTemplate restTemplate;

  public UserApiClient(DiscoveryClient discoveryClient, RestTemplate restTemplate) {
    this.discoveryClient = discoveryClient;
    this.restTemplate = restTemplate;
  }

  @Valid
  public Optional<UserDto> getUserById(long id) {
    String userGetEndpoint = getSomeUserInstanseUri() + "/api/v1/users/" + id;

    ResponseEntity<UserDto> userResponse =
        restTemplate.getForEntity(userGetEndpoint, UserDto.class);

    HttpStatusCode statusCode = userResponse.getStatusCode();

    if (statusCode == HttpStatus.NOT_FOUND) {
      return Optional.empty();
    }

    if (!statusCode.is2xxSuccessful()) {
      throw new ResponseStatusException(statusCode, "Failed to fetch user info");
    }

    return Optional.of(userResponse.getBody());
  }

  private URI getSomeUserInstanseUri() {
    List<ServiceInstance> userServiceInstances = discoveryClient.getInstances("user");

    if (userServiceInstances.isEmpty()) {
      throw new ResponseStatusException(
          HttpStatus.BAD_GATEWAY, "User service is unreachable. Try again later");
    }

    return userServiceInstances.getFirst().getUri();
  }
}
