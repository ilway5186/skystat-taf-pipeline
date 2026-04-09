package com.skystat.core.exception;

public class InvalidStatusException extends BusinessException {
  public InvalidStatusException(String message) {
    super(ErrorCode.INVALID_STATUS, message);
  }

  public InvalidStatusException(ErrorCode errorCode, String message) {
    super(errorCode, message);
  }
}
