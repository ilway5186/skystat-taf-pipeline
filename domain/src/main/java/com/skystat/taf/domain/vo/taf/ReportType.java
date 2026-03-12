package com.skystat.taf.domain.vo.taf;

import java.util.Arrays;

public enum ReportType {
  ROUTINE,  // 정시 예보
  AMD,
  COR,
  CNL,
  NIL;    // 예보 없음

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
