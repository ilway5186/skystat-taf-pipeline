package com.skystat.core.domain.vo.weather.field.temperature;

import com.skystat.core.domain.vo.weather.field.WeatherCode;

public enum TemperatureExtremeType implements WeatherCode {

  MAXIMUM("TX", "Maximum Temperature"),
  MINIMUM("TN", "Minimum Temperature");

  private final String symbol;
  private final String description;

  TemperatureExtremeType(String symbol, String description) {
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
