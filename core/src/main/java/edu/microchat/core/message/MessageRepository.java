package edu.microchat.core.message;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

interface MessageRepository extends JpaRepository<Message, Long> {
  Page<Message> findAllByOrderByTimestampDesc(Pageable pageable);
}
