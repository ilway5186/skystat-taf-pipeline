package com.skystat.core.domain.vo.weather.field.phenomena;

import com.skystat.core.domain.vo.weather.field.WeatherCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public enum WeatherDescriptor implements WeatherCode {

  PATCHES("BC","Patches"),
  BLOWING("BL","Blowing"),
  DRIFTING("DR","Drifting"),
  DISTANT_LIGHTNING("DL","Distant lightning"),
  FREEZING("FZ","Freezing"),
  SHALLOW("MI","Shallow"),
  PARTIAL("PR","Partial"),
  SHOWERS("SH","Showers"),
  THUNDERSTORM("TS","Thunderstorm"),
  VICINITY("VC","in the Vicinity");

  private final String symbol;
  private final String description;

  public static WeatherDescriptor fromSymbol(String symbol) {
    for (WeatherDescriptor descriptor : values()) {
      if (descriptor.symbol.equals(symbol)) {
        return descriptor;
      }
    }
    throw new IllegalArgumentException("No WeatherDescriptor with symbol: " + symbol);
  }

}
