package com.skystat.core.exception;

public enum ErrorCode {

  INVALID_INPUT(ErrorCategory.VALIDATION, "INVALID_INPUT", "Invalid input."),
  INVALID_STATUS(ErrorCategory.DOMAIN_STATE, "INVALID_STATUS", "Cannot process the request in the current status."),

  INVALID_TAF_FORMAT(ErrorCategory.PARSING, "INVALID_TAF_FORMAT", "Invalid TAF format."),

  MISSING_STATION_ICAO(ErrorCategory.PARSING, "MISSING_STATION_ICAO", "ICAO not found in TAF report."),
  MISSING_ISSUED_TIME(ErrorCategory.PARSING, "MISSING_ISSUED_TIME", "Issued time not found in TAF report."),
  MISSING_FORECAST_PERIOD(ErrorCategory.PARSING, "MISSING_FORECAST_PERIOD", "Forecast period not found in TAF report."),
  MISSING_WIND(ErrorCategory.PARSING, "MISSING_WIND", "Wind not found in TAF report."),
  MISSING_VISIBILITY(ErrorCategory.PARSING, "MISSING_VISIBILITY", "Visibility not found in TAF report."),

  INVALID_REPORT_TYPE(ErrorCategory.PARSING, "INVALID_REPORT_TYPE", "Invalid report type token."),
  INVALID_CHANGE_INDICATOR(ErrorCategory.PARSING, "INVALID_CHANGE_INDICATOR", "Invalid change indicator token."),
  PARSING_STATE_ERROR(ErrorCategory.PARSING, "PARSING_STATE_ERROR", "Parser reached an unreachable state.");

  private final ErrorCategory category;
  private final String code;
  private final String defaultMessage;

  ErrorCode(ErrorCategory category, String code, String defaultMessage) {
    this.category = category;
    this.code = code;
    this.defaultMessage = defaultMessage;
  }

  public ErrorCategory category() {
    return category;
  }

  public String code() {
    return code;
  }

  public String defaultMessage() {
    return defaultMessage;
  }

}
