package com.skystat.taf.domain.vo.weather.elements.cloud;

import com.skystat.taf.domain.vo.weather.elements.WeatherCode;

public enum CloudType implements WeatherCode {

  TCU("TCU","Towering Cumulus"),
  CB("CB","Cumulonimbus"),
  NONE("","None");

  private final String symbol;
  private final String description;

  CloudType(String symbol, String description) {
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
