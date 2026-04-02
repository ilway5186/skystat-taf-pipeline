package com.skystat.taf.domain.exception;

public class InvalidStatusException extends BusinessException {
  public InvalidStatusException(String message) {
    super(ErrorCode.INVALID_STATUS, message);
  }
}
