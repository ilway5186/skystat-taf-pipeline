package com.skystat.taf.domain.vo.weather.elements.cloud;

import com.skystat.taf.domain.vo.weather.elements.WeatherCode;

public enum CloudCoverage implements WeatherCode {

  FEW("FEW","Few"),
  SCT("SCT","Scattered"),
  BKN("BKN","Broken"),
  OVC("OVC","Overcast"),
  VV("VV","Vertical visibility"),
  SKC("SKC","Sky clear"),
  CLR("CLR","Clear"),
  NSC("NSC","No significant cloud"),
  NCD("NCD","No cloud detected");

  private final String symbol;
  private final String description;

  CloudCoverage(String symbol, String description) {
    this.symbol = symbol;
    this.description = description;
  }

  public boolean requiresAltitude() {
    return switch (this) {
      case FEW, SCT, BKN, OVC, VV -> true;
      default -> false;
    };
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
