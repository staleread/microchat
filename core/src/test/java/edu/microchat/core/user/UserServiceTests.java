package edu.microchat.core.user;

import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.microchat.core.assistant.AssistantApiClient;
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
  @MockitoBean private AssistantApiClient assistantApiClient;

  @Test
  void whenGetAllItemsListThenSizeIs30() {
    int size = underTest.getAll().size();
    assertEquals(30, size);
  }
}
