package edu.microchat.core.user;

import static org.junit.jupiter.api.Assertions.*;

import edu.microchat.core.assistant.AssistantApiClient;
import edu.microchat.core.common.ApiResponse;
import edu.microchat.core.common.BaseMetadata;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@Sql("/users.sql")
class UserServiceTests {

  @Autowired private UserService underTest;
  @Autowired private UserRepository userRepository;
  @MockitoBean private AssistantApiClient assistantApiClient;

  @Test
  void getAll_UsersExist_ReturnsAllUsers() {
    int size = underTest.getAll().size();
    assertEquals(30, size);
  }

  @Test
  void getAllAsApiResponse_UsersExist_ReturnsSuccessWithAllUsers() {
    ApiResponse<BaseMetadata, UserDto> response = underTest.getAllAsApiResponse();

    assertTrue(response.getMeta().isSuccess());
    assertEquals(200, response.getMeta().getCode());
    assertNull(response.getMeta().getErrorMessage());
    assertEquals(30, response.getData().size());
  }

  @Test
  void getAllAsApiResponse_NoUsers_ReturnsNotFound() {
    userRepository.deleteAll();

    ApiResponse<BaseMetadata, UserDto> response = underTest.getAllAsApiResponse();

    assertFalse(response.getMeta().isSuccess());
    assertEquals(404, response.getMeta().getCode());
    assertNull(response.getData());
  }

  @Test
  void getByIdAsApiResponse_UserExists_ReturnsSuccessWithUser() {
    ApiResponse<BaseMetadata, UserDto> response = underTest.getByIdAsApiResponse(1L);

    assertTrue(response.getMeta().isSuccess());
    assertEquals(200, response.getMeta().getCode());
    assertEquals(1, response.getData().size());
    assertEquals(1L, response.getData().getFirst().id());
  }

  @Test
  void getByIdAsApiResponse_UserNotFound_ReturnsNotFound() {
    ApiResponse<BaseMetadata, UserDto> response = underTest.getByIdAsApiResponse(9999L);

    assertFalse(response.getMeta().isSuccess());
    assertEquals(404, response.getMeta().getCode());
    assertNull(response.getData());
  }

  @Test
  void createAsApiResponse_NewUsername_ReturnsSuccessWithId() {
    var request = new UserCreateRequest("newuser", "password123", "John", "Doe", null, null, Role.STUDENT);

    ApiResponse<BaseMetadata, Long> response = underTest.createAsApiResponse(request);

    assertTrue(response.getMeta().isSuccess());
    assertEquals(200, response.getMeta().getCode());
    assertEquals(1, response.getData().size());
    assertNotNull(response.getData().getFirst());
  }

  @Test
  void createAsApiResponse_DuplicateUsername_ReturnsBadRequest() {
    var request = new UserCreateRequest("dgingell0", "password123", "John", "Doe", null, null, Role.STUDENT);

    ApiResponse<BaseMetadata, Long> response = underTest.createAsApiResponse(request);

    assertFalse(response.getMeta().isSuccess());
    assertEquals(400, response.getMeta().getCode());
    assertNull(response.getData());
  }

  @Test
  void updateAsApiResponse_ValidRequest_ReturnsSuccess() {
    var request = new UserUpdateRequest("newusername", "John", "Doe", null, null);

    ApiResponse<BaseMetadata, Void> response = underTest.updateAsApiResponse(1L, request);

    assertTrue(response.getMeta().isSuccess());
    assertEquals(200, response.getMeta().getCode());
  }

  @Test
  void updateAsApiResponse_UserNotFound_ReturnsNotFound() {
    var request = new UserUpdateRequest("newusername", "John", "Doe", null, null);

    ApiResponse<BaseMetadata, Void> response = underTest.updateAsApiResponse(9999L, request);

    assertFalse(response.getMeta().isSuccess());
    assertEquals(404, response.getMeta().getCode());
  }

  @Test
  void updateAsApiResponse_DuplicateUsername_ReturnsBadRequest() {
    var request = new UserUpdateRequest("plinster1", "John", "Doe", null, null);

    ApiResponse<BaseMetadata, Void> response = underTest.updateAsApiResponse(1L, request);

    assertFalse(response.getMeta().isSuccess());
    assertEquals(400, response.getMeta().getCode());
  }

  @Test
  void deleteAsApiResponse_UserExists_ReturnsSuccess() {
    ApiResponse<BaseMetadata, Void> response = underTest.deleteAsApiResponse(1L);

    assertTrue(response.getMeta().isSuccess());
    assertEquals(200, response.getMeta().getCode());
  }

  @Test
  void deleteAsApiResponse_UserNotFound_ReturnsNotFound() {
    ApiResponse<BaseMetadata, Void> response = underTest.deleteAsApiResponse(9999L);

    assertFalse(response.getMeta().isSuccess());
    assertEquals(404, response.getMeta().getCode());
  }
}
