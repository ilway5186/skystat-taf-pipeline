package com.skystat.taf.ingestion.exception;

import java.util.Map;

public class BusinessException extends RuntimeException {

  private final ErrorCode errorCode;
  private final Map<String, Object> details;

  public BusinessException(ErrorCode errorCode) {
    super(errorCode.defaultMessage());
    this.errorCode = errorCode;
    this.details = Map.of();
  }

  public BusinessException(ErrorCode errorCode, String message) {
    super(message);
    this.errorCode = errorCode;
    this.details = Map.of();
  }

  public BusinessException(ErrorCode errorCode, String message, Map<String, Object> details) {
    super(message);
    this.errorCode = errorCode;
    this.details = details == null ? Map.of() : Map.copyOf(details);
  }

  public ErrorCode errorCode() { return errorCode; }

  public Map<String, Object> details() { return details; }

}
