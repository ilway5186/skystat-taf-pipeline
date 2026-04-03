package com.skystat.taf.ingestion.exception;

public class InvalidInputException extends BusinessException {

  public InvalidInputException(String message) {
    super(ErrorCode.INVALID_INPUT, message);
  }

}
