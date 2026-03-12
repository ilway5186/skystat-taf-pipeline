package com.skystat.taf.domain.vo.taf;

import java.util.Arrays;

public enum ReportType {
  ROUTINE,
  AMD,
  COR,
  CNL,
  NIL;

  public static ReportType from(String token) {
    if (token == null || token.isBlank() || token.equals("TAF")) {
      return ROUTINE;
    }
    return Arrays.stream(values())
      .filter(type -> type.name().equals(token))
      .findFirst()
      .orElseThrow(() -> new IllegalArgumentException("Invalid report type token: " + token));
  }

}
