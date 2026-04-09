package com.skystat.core.domain.vo.weather.field.phenomena;

import com.skystat.core.domain.vo.weather.field.WeatherCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public enum WeatherIntensity implements WeatherCode {

  LIGHT("-", "Light"),
  MODERATE("",  "Moderate"),
  HEAVY("+",  "Heavy");

  private final String symbol;
  private final String description;

  public static WeatherIntensity fromSymbol(String symbol) {
    for (WeatherIntensity intensity : values()) {
      if (intensity.symbol.equals(symbol)) {
        return intensity;
      }
    }
    throw new IllegalArgumentException("Unknown weather intensity symbol: " + symbol);
  }

}
