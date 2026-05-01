package edu.microchat.core.message;

import static java.util.concurrent.TimeUnit.*;
import static org.awaitility.Awaitility.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.microchat.core.assistant.AssistantApiClient;
import edu.microchat.core.assistant.AssistantPromptDto;
import edu.microchat.core.assistant.AssistantReplyEvent;
import edu.microchat.core.user.UserDto;
import edu.microchat.core.user.UserService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(
    username = "student",
    roles = {"STUDENT"})
class MessageIntegrationTests {
  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private MessageRepository messageRepository;

  @MockitoSpyBean private AssistantApiClient assistantApiClient;
  @MockitoBean private UserService userService;
  @Autowired private ApplicationEventPublisher eventPublisher;

  @AfterEach
  void tearDown() {
    messageRepository.deleteAll();
  }

  @Test
  void getAll_ListOfMessages() throws Exception {
    messageRepository.save(new Message(1, "Hello!"));
    messageRepository.save(new Message(2, "Who are you?"));
    messageRepository.save(new Message(1, "My ID is 1"));

    mockMvc
        .perform(get("/api/v1/messages/?page=0&size=2"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data.length()").value(2))
        .andExpect(jsonPath("$.data[0].senderId").value(1))
        .andExpect(jsonPath("$.data[0].content").value("My ID is 1"))
        .andExpect(jsonPath("$.data[1].content").value("Who are you?"));
  }

  @Test
  void create_RegularMessage_CreatesMessage() throws Exception {
    var mockUserDto = new UserDto(1L, "user1", "John", "Doe", null, "bio1");
    when(userService.getById(1L)).thenReturn(mockUserDto);

    var messageRequest = new MessageCreateRequest(1L, "Hello there!");

    String responseContent =
        mockMvc
            .perform(
                post("/api/v1/messages/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(messageRequest)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    var messageId = objectMapper.readTree(responseContent).get("data").get(0).asLong();
    Message createdMessage = messageRepository.findById(messageId).orElse(null);

    assertNotNull(createdMessage);
    assertEquals("Hello there!", createdMessage.getContent());
    assertEquals(MessageSource.USER, createdMessage.getSource());
    verify(assistantApiClient, times(0)).sendAssistantPrompt(any(AssistantPromptDto.class));
  }

  @Test
  void create_AssistantMessage_CreatesUserAndReplyMessages() throws Exception {
    var mockUserDto = new UserDto(1L, "user1", "John", "Doe", null, "bio1");
    when(userService.getById(1L)).thenReturn(mockUserDto);

    var replyText = "Can't complaint, bro";

    doAnswer(
            invocation -> {
              eventPublisher.publishEvent(new AssistantReplyEvent(replyText));
              return null;
            })
        .when(assistantApiClient)
        .sendAssistantPrompt(any(AssistantPromptDto.class));

    var messageRequest = new MessageCreateRequest(1L, "Mr. @assistant, how are you?");

    mockMvc
        .perform(
            post("/api/v1/messages/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(messageRequest)))
        .andExpect(status().isOk());

    await().atMost(5, SECONDS).until(messageRepository::count, Matchers.equalTo(2L));

    Message lastMessage =
        messageRepository.findAllByOrderByTimestampDesc(PageRequest.of(0, 1)).getContent().get(0);

    verify(assistantApiClient, times(1)).sendAssistantPrompt(any(AssistantPromptDto.class));
    assertNotNull(lastMessage);
    assertEquals(replyText, lastMessage.getContent());
    assertEquals(MessageSource.BOT, lastMessage.getSource());
  }

  @Test
  void create_NotPositiveSenderId_BadRequest() throws Exception {
    var messageRequest = new MessageCreateRequest(0L, "Hello!");

    mockMvc
        .perform(
            post("/api/v1/messages/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(messageRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$").isMap())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$.senderId").value("must be greater than 0"));

    verify(userService, times(0)).getById(anyLong());
    verify(assistantApiClient, times(0)).sendAssistantPrompt(any(AssistantPromptDto.class));
  }

  @Test
  void create_BlankContent_BadRequest() throws Exception {
    var messageRequest = new MessageCreateRequest(1L, "");

    mockMvc
        .perform(
            post("/api/v1/messages/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(messageRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$").isMap())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$.content").value("must not be blank"));

    verify(userService, times(0)).getById(anyLong());
    verify(assistantApiClient, times(0)).sendAssistantPrompt(any(AssistantPromptDto.class));
  }

  @Test
  void create_SenderNotFound_BadRequest() throws Exception {
    var messageRequest = new MessageCreateRequest(0L, "Hello!");

    mockMvc
        .perform(
            post("/api/v1/messages/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(messageRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$").isMap())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$.senderId").value("must be greater than 0"));

    verify(userService, times(0)).getById(anyLong());
    verify(assistantApiClient, times(0)).sendAssistantPrompt(any(AssistantPromptDto.class));
  }

  private String toJson(Object object) throws JsonProcessingException {
    return objectMapper.writeValueAsString(object);
  }
}
