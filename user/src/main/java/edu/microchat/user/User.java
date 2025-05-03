package edu.microchat.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "chat_user")
class User extends AbstractPersistable<Long> {
  @Column(nullable = false)
  private String username;

  @Column(nullable = true)
  private String bio;

  public User() {}

  public User(String username) {
    this.username = username;
  }

  public String getUsername() {
    return username;
  }

  public String getBio() {
    return bio;
  }
}
