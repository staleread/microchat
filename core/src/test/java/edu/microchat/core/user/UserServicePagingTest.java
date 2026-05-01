package edu.microchat.core.user;

import static org.junit.jupiter.api.Assertions.*;

import edu.microchat.core.common.ApiResponse;
import edu.microchat.core.common.PaginationMetadata;
import edu.microchat.core.common.PaginationRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@SpringBootTest
@Transactional
@Sql("/users.sql")
class UserServicePagingTest {

  @Autowired private UserService underTest;
  @Autowired private UserRepository userRepository;

  @Test
  void getUsersPageAsApiResponse_HappyPath_ReturnsOk() {
    PaginationRequest request = new PaginationRequest(0, 5);

    ApiResponse<PaginationMetadata, UserDto> response = underTest.getUsersPageAsApiResponse(request);

    assertNotNull(response);
    assertNotNull(response.getMeta());

    assertTrue(response.getMeta().isSuccess());
    assertEquals(200, response.getMeta().getCode());
    assertNull(response.getMeta().getErrorMessage());

    assertEquals(0, response.getMeta().getNumber());
    assertEquals(5, response.getMeta().getSize());
    assertEquals(30, response.getMeta().getTotalElements());
    assertEquals(6, response.getMeta().getTotalPages());
    assertTrue(response.getMeta().isFirst());
    assertFalse(response.getMeta().isLast());

    assertNotNull(response.getData());
    assertEquals(5, response.getData().size());
  }

  @Test
  void getUsersPageAsApiResponse_SizeIs7AndPageIs4_ReturnsIsLastTrueAndSizeEquals2() {
    PaginationRequest request = new PaginationRequest(4, 7);

    ApiResponse<PaginationMetadata, UserDto> response = underTest.getUsersPageAsApiResponse(request);

    assertNotNull(response);
    assertEquals(4, response.getMeta().getNumber());
    assertEquals(7, response.getMeta().getSize());
    assertEquals(30, response.getMeta().getTotalElements());
    assertEquals(5, response.getMeta().getTotalPages());
    assertFalse(response.getMeta().isFirst());
    assertTrue(response.getMeta().isLast());

    assertNotNull(response.getData());
    assertEquals(2, response.getData().size());
  }

  @Test
  void getUsersPageAsApiResponse_NoUsers_ReturnsEmptyPageWithMetadata() {
    userRepository.deleteAll();
    PaginationRequest request = new PaginationRequest(0, 10);

    ApiResponse<PaginationMetadata, UserDto> response = underTest.getUsersPageAsApiResponse(request);

    assertNotNull(response);
    assertNotNull(response.getMeta());
    assertNotNull(response.getData());

    assertTrue(response.getMeta().isSuccess());
    assertEquals(0, response.getMeta().getTotalElements());
    assertTrue(response.getData().isEmpty());
  }

  @Test
  void getUsersPageAsApiResponse_InvalidPageNumber_ThrowsResponseStatusException() {
    PaginationRequest request = new PaginationRequest(-1, 10);

    assertThrows(
        ResponseStatusException.class, () -> underTest.getUsersPageAsApiResponse(request));
  }

  @Test
  void getUsersPageAsApiResponse_PageNumberOutsideOfTotalPages_ThrowsResponseStatusException() {
    PaginationRequest request = new PaginationRequest(10, 10); // total 30 elements, 3 pages (0,1,2)

    assertThrows(
        ResponseStatusException.class, () -> underTest.getUsersPageAsApiResponse(request));
  }

  @Test
  void getUsersPageAsApiResponse_ZeroPageSize_ThrowsResponseStatusException() {
    PaginationRequest request = new PaginationRequest(0, 0);

    assertThrows(
        ResponseStatusException.class, () -> underTest.getUsersPageAsApiResponse(request));
  }
}
