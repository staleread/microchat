package edu.microchat.message;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import java.time.LocalDateTime;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
class Message extends AbstractPersistable<Long> {
  @Column(nullable = false, updatable = false)
  private long senderId;

  @Column(nullable = false, updatable = false)
  private String content;

  @Column(nullable = false, updatable = true)
  private boolean isProcessed;

  @Column(nullable = false, updatable = false)
  private LocalDateTime timestamp;

  public Message() {}

  public Message(long senderId, String content) {
    this.senderId = senderId;
    this.content = content;
    this.timestamp = LocalDateTime.now();
  }

  public void markAsProcessed() {
    this.isProcessed = true;
  }

  public long getSenderId() {
    return senderId;
  }

  public String getContent() {
    return content;
  }

  public boolean isProcessed() {
    return isProcessed;
  }

  public LocalDateTime getTimestamp() {
    return timestamp;
  }
}
