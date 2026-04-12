package edu.microchat.core.message;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Getter
@NoArgsConstructor
class Message extends AbstractPersistable<Long> {
  public static final String ASSITANT_MENTION = "@assistant";
  public static final long ASSISTANT_ID = 0;

  @Column(nullable = false, updatable = false)
  private long senderId;

  @Column(nullable = false, updatable = false)
  private String content;

  @Column(nullable = false, updatable = false)
  private LocalDateTime timestamp;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, updatable = false)
  private MessageSource source;

  public static Message createAssistantMessage(String content) {
    return new Message(ASSISTANT_ID, content, MessageSource.BOT);
  }

  public Message(long senderId, String content) {
    this(senderId, content, MessageSource.USER);
  }

  private Message(long senderId, String content, MessageSource source) {
    this.senderId = senderId;
    this.content = content;
    this.source = source;
    this.timestamp = LocalDateTime.now();
  }

  public boolean isAssistantPrompt() {
    return content.contains(ASSITANT_MENTION);
  }
}
