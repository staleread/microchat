package edu.microchat.core.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class BaseMetadata {
  private int code;
  private boolean success;
  private String errorMessage;

  public BaseMetadata(int code, boolean success) {
    this.code = code;
    this.success = success;
  }

  public static BaseMetadata success(int code) {
    return BaseMetadata.builder().code(code).success(true).build();
  }

  public static BaseMetadata error(int code, String message) {
    return BaseMetadata.builder().code(code).success(false).errorMessage(message).build();
  }
}
