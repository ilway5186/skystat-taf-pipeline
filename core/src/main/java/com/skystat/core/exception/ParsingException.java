package com.skystat.core.exception;

public class ParsingException extends BusinessException {

  public ParsingException(ErrorCode errorCode, String reportText) {
    super(errorCode, errorCode.defaultMessage() + ": " + reportText);
  }

}
