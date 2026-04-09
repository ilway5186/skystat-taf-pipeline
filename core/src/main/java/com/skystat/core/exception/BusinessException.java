package com.skystat.core.exception;

import java.util.Map;
import java.util.Objects;

public class BusinessException extends RuntimeException {

  private final ErrorCode errorCode;
  private final Map<String, Object> details;

  public BusinessException(ErrorCode errorCode) {
    super(Objects.requireNonNull(errorCode, "errorCode cannot be null.").defaultMessage());
    this.errorCode = errorCode;
    this.details = Map.of();
  }

  public BusinessException(ErrorCode errorCode, String message) {
    super(message);
    this.errorCode = Objects.requireNonNull(errorCode, "errorCode cannot be null.");
    this.details = Map.of();
  }

  public BusinessException(ErrorCode errorCode, Map<String, Object> details) {
    super(Objects.requireNonNull(errorCode, "errorCode cannot be null.").defaultMessage());
    this.errorCode = errorCode;
    this.details = details == null ? Map.of() : Map.copyOf(details);
  }

  public BusinessException(ErrorCode errorCode, String message, Map<String, Object> details) {
    super(message);
    this.errorCode = Objects.requireNonNull(errorCode, "errorCode cannot be null.");
    this.details = details == null ? Map.of() : Map.copyOf(details);
  }

  public ErrorCode errorCode() { return errorCode; }

  public Map<String, Object> details() { return details; }

}
