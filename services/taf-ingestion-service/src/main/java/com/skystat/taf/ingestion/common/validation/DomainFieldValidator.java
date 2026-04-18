package com.skystat.taf.ingestion.common.validation;

import com.skystat.taf.ingestion.common.exception.IngestionException;

import static com.skystat.taf.ingestion.common.exception.IngestionErrorCode.INVALID_INPUT;

public final class DomainFieldValidator {

  private DomainFieldValidator() {
  }

  public static String requireNonBlank(String value, String fieldName) {
    if (value == null || value.isBlank()) {
      throw new IngestionException(INVALID_INPUT, fieldName + " cannot be blank.");
    }
    return value;
  }

  public static <T> T requireNonNull(T value, String fieldName) {
    if (value == null) {
      throw new IngestionException(INVALID_INPUT, fieldName + " cannot be null.");
    }
    return value;
  }

}
