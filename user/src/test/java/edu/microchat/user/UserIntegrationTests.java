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
  void getAll_ListOfUsers() throws Exception {
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
  void create_CreatesUser() throws Exception {
    var request = new UserCreateRequest("test1", "test_bio1");

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
    assertEquals("test_bio1", createdUser.getBio());
  }

  @Test
  void create_NoBio_CreatesUser() throws Exception {
    var request = new UserCreateRequest("test1", null);

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
    assertNull(createdUser.getBio());
  }

  @Test
  void create_MinBioLength_CreatesUser() throws Exception {
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
    User createdUser = userRepository.findById(userId).orElseThrow();

    assertEquals("bio1", createdUser.getBio());
  }

  @Test
  void create_MaxBioLength_CreatesUser() throws Exception {
    var request =
        new UserCreateRequest(
            "test1",
            "123456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789 ");

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
    User createdUser = userRepository.findById(userId).orElseThrow();

    assertEquals(
        "123456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789 ",
        createdUser.getBio());
  }

  @Test
  void create_BlankUsername_BadRequest() throws Exception {
    var request = new UserCreateRequest("", "bio1");

    mockMvc
        .perform(
            post("/api/v1/users/").contentType(MediaType.APPLICATION_JSON).content(toJson(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$").isMap())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$.username").value("must not be blank"));
  }

  @Test
  void create_TooShortBio_BadRequest() throws Exception {
    var request = new UserCreateRequest("username", "bio");

    mockMvc
        .perform(
            post("/api/v1/users/").contentType(MediaType.APPLICATION_JSON).content(toJson(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$").isMap())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$.bio").value("length must be between 4 and 100"));
  }

  @Test
  void create_TooLongBio_BadRequest() throws Exception {
    var request =
        new UserCreateRequest(
            "username",
            "123456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789 1");

    mockMvc
        .perform(
            post("/api/v1/users/").contentType(MediaType.APPLICATION_JSON).content(toJson(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$").isMap())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$.bio").value("length must be between 4 and 100"));
  }

  @Test
  void create_DuplicateUsername_BadRequest() throws Exception {
    userRepository.save(new User("test1"));

    var request = new UserCreateRequest("test1", "bio1");

    mockMvc
        .perform(
            post("/api/v1/users/").contentType(MediaType.APPLICATION_JSON).content(toJson(request)))
        .andExpect(status().isBadRequest());
  }

  private String toJson(Object object) throws JsonProcessingException {
    return objectMapper.writeValueAsString(object);
  }
}
