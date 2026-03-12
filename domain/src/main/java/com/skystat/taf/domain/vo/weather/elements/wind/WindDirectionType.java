package com.skystat.taf.domain.vo.weather.elements.wind;

import com.skystat.taf.domain.vo.weather.elements.WeatherCode;

public enum WindDirectionType implements WeatherCode {

  FIX("FIX", "fixed"),
  VRB("VRB", "variable");

  private final String symbol;
  private final String description;

  WindDirectionType(String symbol, String description) {
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
