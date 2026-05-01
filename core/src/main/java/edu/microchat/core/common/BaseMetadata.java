package edu.microchat.core.common;

import lombok.Getter;
import lombok.AllArgsConstructor;

@Getter
@AllArgsConstructor
public class BaseMetadata {
  private int code;
  private boolean success;
  private String errorMessage;

  public BaseMetadata(int code, boolean success) {
    this.code = code;
    this.success = success;
  }

  public static BaseMetadata success(int code) {
    return new BaseMetadata(code, true, null);
  }

  public static BaseMetadata error(int code, String message) {
    return new BaseMetadata(code, false, message);
  }
}
