package edu.microchat.message.message;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import java.time.LocalDateTime;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
class Message extends AbstractPersistable<Long> {
  public static final String ASSITANT_MENTION = "/assistant";
  public static final long ASSISTANT_ID = 0;

  @Column(nullable = false, updatable = false)
  private long senderId;

  @Column(nullable = false, updatable = false)
  private String content;

  @Column(nullable = false, updatable = false)
  private LocalDateTime timestamp;

  public static Message assistantMessage(String content) {
    return new Message(ASSISTANT_ID, content);
  }

  public Message() {}

  public Message(long senderId, String content) {
    this.senderId = senderId;
    this.content = content;
    this.timestamp = LocalDateTime.now();
  }

  public long getSenderId() {
    return senderId;
  }

  public String getContent() {
    return content;
  }

  public LocalDateTime getTimestamp() {
    return timestamp;
  }

  public boolean isAssistantPrompt() {
    return content.contains(ASSITANT_MENTION);
  }
}
