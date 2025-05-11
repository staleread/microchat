package edu.microchat.user;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class UserIntegrationTests {
  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private UserRepository userRepository;

  @AfterEach
  void tearsDown() {
    userRepository.deleteAll();
  }

  @Test
  void getAll_ListOf3Users() throws Exception {
    var users = List.of(new User("test1", "bio1"), new User("test2"), new User("test3"));
    userRepository.saveAll(users);

    mockMvc
        .perform(get("/api/v1/users/"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(3))
        .andExpect(jsonPath("$[0].bio").value("bio1"))
        .andExpect(jsonPath("$[0].username").value("test1"))
        .andExpect(jsonPath("$[2].username").value("test3"));
  }

  @Test
  void getAll_NoUsers_EmptyList() throws Exception {
    mockMvc
        .perform(get("/api/v1/users/"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(0));
  }

  @Test
  void getById_ExistentUser() throws Exception {
    var user = new User("test1", "bio1");
    long userId = userRepository.save(user).getId();

    mockMvc
        .perform(get("/api/v1/users/" + userId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value("test1"))
        .andExpect(jsonPath("$.bio").value("bio1"));
  }

  @Test
  void getById_InvalidUserId_BadRequest() throws Exception {
    mockMvc.perform(get("/api/v1/users/0")).andExpect(status().isBadRequest());
  }

  @Test
  void getById_InexistentUserId_NotFound() throws Exception {
    mockMvc.perform(get("/api/v1/users/1")).andExpect(status().isNotFound());
  }

  @Test
  void create_ValidUserRequest_CreatesUser() throws Exception {
    var request = new UserCreateRequest("test1", "bio1");

    MockHttpServletResponse response =
        mockMvc
            .perform(
                post("/api/v1/users/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse();

    var userId = Long.parseLong(response.getContentAsString());
    User createdUser = userRepository.findById(userId).orElse(null);

    assertNotNull(createdUser);
    assertEquals("test1", createdUser.getUsername());
    assertEquals("bio1", createdUser.getBio());
  }

  @Test
  void create_UsernameIsTaken_BadRequest() throws Exception {
    userRepository.save(new User("nicolas"));

    var request = new UserCreateRequest("nicolas", "semidev");

    mockMvc
        .perform(
            post("/api/v1/users/").contentType(MediaType.APPLICATION_JSON).content(toJson(request)))
        .andExpect(status().isBadRequest());
  }

  private String toJson(Object object) throws JsonProcessingException {
    return objectMapper.writeValueAsString(object);
  }
}
