package edu.microchat.message.message;

import static java.util.concurrent.TimeUnit.*;
import static org.awaitility.Awaitility.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.microchat.message.assistant.AssistantApiClient;
import edu.microchat.message.assistant.AssistantPromptDto;
import edu.microchat.message.assistant.AssistantReplyDto;
import edu.microchat.message.user.UserApiClient;
import edu.microchat.message.user.UserDto;
import java.util.Optional;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class MessageIntegrationTests {
  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private MessageRepository messageRepository;

  @MockitoSpyBean private AssistantApiClient assistantApiClient;
  @MockitoBean private UserApiClient userApiClient;

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
        .perform(get("/api/v1/messages/?page=0&count=2"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].senderId").value(1))
        .andExpect(jsonPath("$[0].content").value("My ID is 1"))
        .andExpect(jsonPath("$[1].content").value("Who are you?"));
  }

  @Test
  void create_RegularMessage_CreatesMessage() throws Exception {
    var mockUserDto = new UserDto(1L, "user1", "bio1");
    when(userApiClient.getUserById(1L)).thenReturn(Optional.of(mockUserDto));

    var messageRequest = new MessageCreateRequest(1L, "Hello there!");

    MockHttpServletResponse response =
        mockMvc
            .perform(
                post("/api/v1/messages/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(messageRequest)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse();

    var messageId = Long.parseLong(response.getContentAsString());
    Message createdMessage = messageRepository.findById(messageId).orElse(null);

    assertNotNull(createdMessage);
    assertEquals("Hello there!", createdMessage.getContent());
    verify(assistantApiClient, times(0)).sendAssistantPrompt(any(AssistantPromptDto.class));
  }

  @Test
  void create_AssistantMessage_CreatesUserAndReplyMessages() throws Exception {
    var mockUserDto = new UserDto(1L, "user1", "bio1");
    when(userApiClient.getUserById(1L)).thenReturn(Optional.of(mockUserDto));

    var mockReplyDto = new AssistantReplyDto("Can't complaint, bro");

    doAnswer(
            invocation -> {
              assistantApiClient.handleAssistantReply(mockReplyDto);
              return null;
            })
        .when(assistantApiClient)
        .sendAssistantPrompt(any(AssistantPromptDto.class));

    var messageRequest = new MessageCreateRequest(1L, "Mr. /assistant, how are you?");

    mockMvc
        .perform(
            post("/api/v1/messages/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(messageRequest)))
        .andExpect(status().isOk());

    await().atMost(5, SECONDS).until(messageRepository::count, Matchers.equalTo(2L));

    Message lastMessage =
        messageRepository.findAllByOrderByTimestampDesc(PageRequest.of(0, 1)).getFirst();

    verify(assistantApiClient, times(1)).sendAssistantPrompt(any(AssistantPromptDto.class));
    assertNotNull(lastMessage);
    assertEquals("Can't complaint, bro", lastMessage.getContent());
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

    verify(userApiClient, times(0)).getUserById(anyLong());
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

    verify(userApiClient, times(0)).getUserById(anyLong());
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

    verify(userApiClient, times(0)).getUserById(anyLong());
    verify(assistantApiClient, times(0)).sendAssistantPrompt(any(AssistantPromptDto.class));
  }

  private String toJson(Object object) throws JsonProcessingException {
    return objectMapper.writeValueAsString(object);
  }
}
