package com.skystat.taf.domain.vo.taf;

import java.util.Arrays;

public enum ChangeIndicator {
  HEADER,
  BECMG,
  FM,
  TEMPO,
  INTER,
  PROB30,
  PROB40,
  PROB30_TEMPO,
  PROB40_TEMPO,
  NONE;

  public static ChangeIndicator from(String token) {
    if (token == null || token.isBlank()) {
      return HEADER;
    }

    return Arrays.stream(values())
      .filter(indicator -> indicator.name().equals(token))
      .findFirst()
      .orElseThrow(() -> new IllegalArgumentException("Unknown change indicator token: " + token));
  }

}
