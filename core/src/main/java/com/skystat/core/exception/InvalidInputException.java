package com.skystat.core.exception;

public class InvalidInputException extends BusinessException {

  public InvalidInputException(String message) {
    super(ErrorCode.INVALID_INPUT, message);
  }

  public InvalidInputException(ErrorCode errorCode, String message) {
    super(errorCode, message);
  }

}
