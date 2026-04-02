package com.skystat.taf.domain.vo.weather.field.cloud;

import com.skystat.taf.domain.vo.weather.field.WeatherCode;

public enum CloudType implements WeatherCode {

  TOWERING_CUMULUS("TCU","Towering Cumulus"),
  CUMULONIMBUS("CB","Cumulonimbus"),
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
