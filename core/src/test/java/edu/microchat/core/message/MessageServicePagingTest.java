package edu.microchat.core.message;

import static org.junit.jupiter.api.Assertions.*;

import edu.microchat.core.common.ApiResponse;
import edu.microchat.core.common.PaginationMetadata;
import edu.microchat.core.common.PaginationRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.server.ResponseStatusException;

@SpringBootTest
class MessageServicePagingTest {

  @Autowired private MessageService underTest;
  @Autowired private MessageRepository messageRepository;

  @BeforeEach
  void setUp() {
    for (int i = 0; i < 15; i++) {
      messageRepository.save(new Message(1, "Message " + i));
    }
  }

  @AfterEach
  void tearDown() {
    messageRepository.deleteAll();
  }

  @Test
  void getMessagesPageAsApiResponse_HappyPath_ReturnsOk() {
    PaginationRequest request = new PaginationRequest(0, 5);

    ApiResponse<PaginationMetadata, MessageDto> response =
        underTest.getMessagesPageAsApiResponse(request);

    assertNotNull(response);
    assertNotNull(response.getMeta());

    assertTrue(response.getMeta().isSuccess());
    assertEquals(200, response.getMeta().getCode());

    assertEquals(0, response.getMeta().getNumber());
    assertEquals(5, response.getMeta().getSize());
    assertEquals(15, response.getMeta().getTotalElements());
    assertEquals(3, response.getMeta().getTotalPages());
    assertTrue(response.getMeta().isFirst());
    assertFalse(response.getMeta().isLast());

    assertNotNull(response.getData());
    assertEquals(5, response.getData().size());
  }

  @Test
  void getMessagesPageAsApiResponse_SizeIs4AndPageIs3_ReturnsIsLastTrueAndSizeEquals3() {
    PaginationRequest request = new PaginationRequest(3, 4);

    ApiResponse<PaginationMetadata, MessageDto> response =
        underTest.getMessagesPageAsApiResponse(request);

    assertNotNull(response);
    assertEquals(3, response.getMeta().getNumber());
    assertEquals(4, response.getMeta().getSize());
    assertEquals(15, response.getMeta().getTotalElements());
    assertEquals(4, response.getMeta().getTotalPages());
    assertFalse(response.getMeta().isFirst());
    assertTrue(response.getMeta().isLast());

    assertNotNull(response.getData());
    assertEquals(3, response.getData().size());
  }

  @Test
  void getMessagesPageAsApiResponse_NoMessages_ReturnsEmptyPageWithMetadata() {
    messageRepository.deleteAll();
    PaginationRequest request = new PaginationRequest(0, 10);

    ApiResponse<PaginationMetadata, MessageDto> response =
        underTest.getMessagesPageAsApiResponse(request);

    assertNotNull(response);
    assertNotNull(response.getMeta());
    assertNotNull(response.getData());

    assertTrue(response.getMeta().isSuccess());
    assertEquals(0, response.getMeta().getTotalElements());
    assertTrue(response.getData().isEmpty());
  }

  @Test
  void getMessagesPageAsApiResponse_InvalidPageNumber_ThrowsResponseStatusException() {
    PaginationRequest request = new PaginationRequest(-1, 10);

    assertThrows(
        ResponseStatusException.class, () -> underTest.getMessagesPageAsApiResponse(request));
  }

  @Test
  void getMessagesPageAsApiResponse_PageNumberOutsideOfTotalPages_ThrowsResponseStatusException() {
    PaginationRequest request = new PaginationRequest(5, 5); // 15 elements, 3 pages (0,1,2)

    assertThrows(
        ResponseStatusException.class, () -> underTest.getMessagesPageAsApiResponse(request));
  }

  @Test
  void getMessagesPageAsApiResponse_ZeroPageSize_ThrowsResponseStatusException() {
    PaginationRequest request = new PaginationRequest(0, 0);

    assertThrows(
        ResponseStatusException.class, () -> underTest.getMessagesPageAsApiResponse(request));
  }
}
