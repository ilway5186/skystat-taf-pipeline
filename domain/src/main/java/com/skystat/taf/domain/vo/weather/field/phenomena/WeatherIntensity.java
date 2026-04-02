package com.skystat.taf.domain.vo.weather.field.phenomena;

import com.skystat.taf.domain.vo.weather.field.WeatherCode;

public enum WeatherIntensity implements WeatherCode {

  LIGHT("-", "Light"),
  MODERATE("",  "Moderate"),
  HEAVY("+",  "Heavy");

  private final String symbol;
  private final String description;

  WeatherIntensity(String symbol, String description) {
    this.symbol = symbol;
    this.description = description;
  }

  @Override
  public String symbol() {
    return symbol;
  }

  @Override
  public String description() {
    return description;
  }

}
