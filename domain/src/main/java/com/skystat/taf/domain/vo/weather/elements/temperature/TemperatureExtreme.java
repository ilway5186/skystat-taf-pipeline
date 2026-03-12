package com.skystat.taf.domain.vo.weather.elements.temperature;

import com.skystat.taf.domain.vo.weather.elements.WeatherCode;

public enum TemperatureExtreme implements WeatherCode {

  MAXIMUM("TX", "Maximum Temperature"),
  MINIMUM("TN", "Minimum Temperature");

  private final String symbol;
  private final String description;

  TemperatureExtreme(String symbol, String description) {
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
