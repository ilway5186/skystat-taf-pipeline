package com.skystat.core.ingestion.exception;

public class InvalidStatusException extends BusinessException {
  public InvalidStatusException(String message) {
    super(ErrorCode.INVALID_STATUS, message);
  }
}
