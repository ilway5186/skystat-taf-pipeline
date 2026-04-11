package com.skystat.taf.ingestion.common.exception;

import java.util.Map;

public class IngestionException extends RuntimeException {

  private final IngestionErrorCode errorCode;
  private final Map<String, Object> details;

  public IngestionException(IngestionErrorCode errorCode) {
    super(errorCode.defaultMessage());
    this.errorCode = errorCode;
    this.details = Map.of();
  }

  public IngestionException(IngestionErrorCode errorCode, String message) {
    super(message);
    this.errorCode = errorCode;
    this.details = Map.of();
  }

  public IngestionException(IngestionErrorCode errorCode, String message, Map<String, Object> details) {
    super(message);
    this.errorCode = errorCode;
    this.details = details == null ? Map.of() : Map.copyOf(details);
  }

  public IngestionErrorCode errorCode() { return errorCode; }

  public Map<String, Object> details() { return details; }

}
