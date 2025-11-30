package edu.microchat.core;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import edu.microchat.core.assistant.AssistantApiClient;
import edu.microchat.core.user.UserResponse;
import edu.microchat.core.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AccessTests {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private UserService userService;
  @MockitoBean private AssistantApiClient assistantApiClient;

  @org.junit.jupiter.api.BeforeEach
  void setUp() {
    when(userService.getById(anyLong()))
        .thenReturn(new UserResponse(53L, "testuser", "Test user bio"));
  }

  @Test
  @WithAnonymousUser
  public void testHomepage() throws Exception {
    mockMvc.perform(get("/index.html")).andExpect(status().isOk());
  }

  @Test
  @WithAnonymousUser
  public void testHealthCheck() throws Exception {
    mockMvc.perform(get("/actuator/health")).andExpect(status().isOk());
  }

  @Test
  @WithAnonymousUser
  public void testGetMotoWithoutCredentials() throws Exception {
    mockMvc.perform(get("/api/v1/messages/moto")).andExpect(status().isUnauthorized());
  }

  @Test
  @WithMockUser(
      username = "guest",
      password = "secret",
      roles = {"GUEST"})
  public void testGetMotoAsGuest() throws Exception {
    mockMvc.perform(get("/api/v1/messages/moto")).andExpect(status().isOk());
  }

  @Test
  @WithMockUser(
      username = "user",
      password = "secret",
      roles = {"USER", "GUEST"})
  public void testGetMotoAsUser() throws Exception {
    mockMvc.perform(get("/api/v1/messages/moto")).andExpect(status().isOk());
  }

  @Test
  @WithMockUser(
      username = "admin",
      password = "secret",
      roles = {"ADMIN", "USER", "GUEST"})
  public void testGetMotoAsAdmin() throws Exception {
    mockMvc.perform(get("/api/v1/messages/moto")).andExpect(status().isOk());
  }

  @Test
  @WithAnonymousUser
  public void testGetMessagesWithoutCredentials() throws Exception {
    mockMvc.perform(get("/api/v1/messages/?page=0&count=5")).andExpect(status().isUnauthorized());
  }

  @Test
  @WithMockUser(
      username = "guest",
      password = "secret",
      roles = {"GUEST"})
  public void testGetMessagesAsGuest() throws Exception {
    mockMvc.perform(get("/api/v1/messages/?page=0&count=5")).andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(
      username = "user",
      password = "secret",
      roles = {"USER", "GUEST"})
  public void testGetMessagesAsUser() throws Exception {
    mockMvc.perform(get("/api/v1/messages/?page=0&count=5")).andExpect(status().isOk());
  }

  @Test
  @WithMockUser(
      username = "admin",
      password = "secret",
      roles = {"ADMIN", "USER", "GUEST"})
  public void testGetMessagesAsAdmin() throws Exception {
    mockMvc.perform(get("/api/v1/messages/?page=0&count=5")).andExpect(status().isOk());
  }

  @Test
  @WithAnonymousUser
  public void testCreateMessageWithoutCredentials() throws Exception {
    mockMvc
        .perform(
            post("/api/v0/messages/")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"senderId\": 53, \"content\": \"Hello!\"}"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @WithMockUser(
      username = "user",
      password = "secret",
      roles = {"USER", "GUEST"})
  public void testCreateMessageAsUser() throws Exception {
    mockMvc
        .perform(
            post("/api/v1/messages/")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"senderId\": 53, \"content\": \"Hello!\"}"))
        .andExpect(status().isOk());
  }

  @Test
  @WithMockUser(
      username = "admin",
      password = "secret",
      roles = {"ADMIN", "USER", "GUEST"})
  public void testCreateMessageAsAdmin() throws Exception {
    mockMvc
        .perform(
            post("/api/v1/messages/")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"senderId\": 53, \"content\": \"Hello from admin!\"}"))
        .andExpect(status().isOk());
  }

  @Test
  @WithAnonymousUser
  public void testGetAllUsersWithoutCredentials() throws Exception {
    mockMvc.perform(get("/api/v1/users/")).andExpect(status().isUnauthorized());
  }

  @Test
  @WithMockUser(
      username = "guest",
      password = "secret",
      roles = {"GUEST"})
  public void testGetAllUsersAsGuest() throws Exception {
    mockMvc.perform(get("/api/v1/users/")).andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(
      username = "user",
      password = "secret",
      roles = {"USER"})
  public void testGetAllUsersAsUser() throws Exception {
    mockMvc.perform(get("/api/v1/users/")).andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(
      username = "admin",
      password = "secret",
      roles = {"ADMIN", "USER", "GUEST"})
  public void testGetAllUsersAsAdmin() throws Exception {
    mockMvc.perform(get("/api/v1/users/")).andExpect(status().isOk());
  }

  @Test
  @WithAnonymousUser
  public void testGetUserByIdWithoutCredentials() throws Exception {
    mockMvc.perform(get("/api/v1/users/2")).andExpect(status().isUnauthorized());
  }

  @Test
  @WithMockUser(
      username = "guest",
      password = "secret",
      roles = {"GUEST"})
  public void testGetUserByIdAsGuest() throws Exception {
    mockMvc.perform(get("/api/v1/users/2")).andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(
      username = "user",
      password = "secret",
      roles = {"USER"})
  public void testGetUserByIdAsUser() throws Exception {
    mockMvc.perform(get("/api/v1/users/2")).andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(
      username = "admin",
      password = "secret",
      roles = {"ADMIN", "USER", "GUEST"})
  public void testGetUserByIdAsAdmin() throws Exception {
    mockMvc.perform(get("/api/v1/users/2")).andExpect(status().isOk());
  }

  @Test
  @WithAnonymousUser
  public void testCreateUserWithoutCredentials() throws Exception {
    mockMvc
        .perform(
            post("/api/v1/users/")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\": \"Misha\", \"bio\": \"The guy from Lab 5\"}"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @WithMockUser(
      username = "guest",
      password = "secret",
      roles = {"GUEST"})
  public void testCreateUserAsGuest() throws Exception {
    mockMvc
        .perform(
            post("/api/v1/users/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"username\": \"Misha\", \"password\": \"secret123\", \"bio\": \"The guy from Lab 5\", \"roles\": [\"USER\", \"GUEST\"]}"))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(
      username = "user",
      password = "secret",
      roles = {"USER", "GUEST"})
  public void testCreateUserAsUser() throws Exception {
    mockMvc
        .perform(
            post("/api/v1/users/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"username\": \"Misha\", \"password\": \"secret123\", \"bio\": \"The guy from Lab 5\", \"roles\": [\"USER\", \"GUEST\"]}"))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(
      username = "admin",
      password = "secret",
      roles = {"ADMIN", "USER", "GUEST"})
  public void testCreateUserAsAdmin() throws Exception {
    mockMvc
        .perform(
            post("/api/v1/users/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"username\": \"Misha\", \"password\": \"secret123\", \"bio\": \"The guy from Lab 5\", \"roles\": [\"USER\", \"GUEST\"]}"))
        .andExpect(status().isOk());
  }

  @Test
  @WithAnonymousUser
  public void testUpdateUserWithoutCredentials() throws Exception {
    mockMvc
        .perform(
            put("/api/v1/users/102")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\": \"Misha\", \"bio\": \"I will stay here forever\"}"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @WithMockUser(
      username = "guest",
      password = "secret",
      roles = {"GUEST"})
  public void testUpdateUserAsGuest() throws Exception {
    mockMvc
        .perform(
            put("/api/v1/users/102")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\": \"Misha\", \"bio\": \"I will stay here forever\"}"))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(
      username = "user",
      password = "secret",
      roles = {"USER"})
  public void testUpdateUserAsUser() throws Exception {
    mockMvc
        .perform(
            put("/api/v1/users/102")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\": \"Misha\", \"bio\": \"I will stay here forever\"}"))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(
      username = "admin",
      password = "secret",
      roles = {"ADMIN", "USER", "GUEST"})
  public void testUpdateUserAsAdmin() throws Exception {
    mockMvc
        .perform(
            put("/api/v1/users/102")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\": \"Misha\", \"bio\": \"I will stay here forever\"}"))
        .andExpect(status().isOk());
  }

  @Test
  @WithAnonymousUser
  public void testDeleteUserWithoutCredentials() throws Exception {
    mockMvc.perform(delete("/api/v1/users/1")).andExpect(status().isUnauthorized());
  }

  @Test
  @WithMockUser(
      username = "guest",
      password = "secret",
      roles = {"GUEST"})
  public void testDeleteUserAsGuest() throws Exception {
    mockMvc.perform(delete("/api/v1/users/1")).andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(
      username = "user",
      password = "secret",
      roles = {"USER"})
  public void testDeleteUserAsUser() throws Exception {
    mockMvc.perform(delete("/api/v1/users/1")).andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(
      username = "admin",
      password = "secret",
      roles = {"ADMIN", "USER", "GUEST"})
  public void testDeleteUserAsAdmin() throws Exception {
    mockMvc.perform(delete("/api/v1/users/103")).andExpect(status().isOk());
  }
}
