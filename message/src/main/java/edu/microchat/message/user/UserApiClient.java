package edu.microchat.message.user;

import jakarta.validation.Valid;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class UserApiClient {
  private final DiscoveryClient discoveryClient;
  private final RestTemplate restTemplate;

  public UserApiClient(DiscoveryClient discoveryClient, RestTemplate restTemplate) {
    this.discoveryClient = discoveryClient;
    this.restTemplate = restTemplate;
  }

  @Valid
  public UserDto getUserById(long id) {
    ServiceInstance userServiceInstance = discoveryClient.getInstances("user").getFirst();
    String userGetEndpoint = userServiceInstance.getUri() + "/api/v1/users/" + id;

    ResponseEntity<UserDto> userResponse =
        restTemplate.getForEntity(userGetEndpoint, UserDto.class);

    if (!userResponse.getStatusCode().is2xxSuccessful()) {
      throw new IllegalStateException("Failed to fetch user info");
    }

    return userResponse.getBody();
  }
}
