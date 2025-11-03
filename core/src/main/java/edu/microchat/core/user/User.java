package edu.microchat.core.user;

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

  public User(String username, String bio) {
    this.username = username;
    this.bio = bio;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getBio() {
    return bio;
  }

  public void setBio(String bio) {
    this.bio = bio;
  }
}
