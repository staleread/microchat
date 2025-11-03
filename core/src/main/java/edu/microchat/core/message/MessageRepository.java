package edu.microchat.core.message;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.ListCrudRepository;

interface MessageRepository extends ListCrudRepository<Message, Long> {
  List<Message> findAllByOrderByTimestampDesc(Pageable pageable);
}
