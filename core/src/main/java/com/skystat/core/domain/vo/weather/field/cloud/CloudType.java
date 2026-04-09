package com.skystat.core.domain.vo.weather.field.cloud;

import com.skystat.core.domain.vo.weather.field.WeatherCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public enum CloudType implements WeatherCode {

  TOWERING_CUMULUS("TCU","Towering Cumulus"),
  CUMULONIMBUS("CB","Cumulonimbus"),
  NONE("","None");

  private final String symbol;
  private final String description;

  public static CloudType fromSymbol(String symbol) {
    for (CloudType cloudType : values()) {
      if (cloudType.symbol.equals(symbol)) {
        return cloudType;
      }
    }
    throw new IllegalArgumentException("Unknown cloud type symbol: " + symbol);
  }

}
