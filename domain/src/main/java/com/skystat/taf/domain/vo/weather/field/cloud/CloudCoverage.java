package com.skystat.taf.domain.vo.weather.field.cloud;

import com.skystat.taf.domain.vo.weather.field.WeatherCode;

public enum CloudCoverage implements WeatherCode {

  FEW("FEW","Few"),
  SCATTERED("SCT","Scattered"),
  BROKEN("BKN","Broken"),
  OVERCAST("OVC","Overcast"),
  VERTICAL_VISIBILITY("VV","Vertical visibility"),
  SKY_CLEAR("SKC","Sky clear"),
  CLEAR("CLR","Clear"),
  NO_SIGNIFICANT_CLOUD("NSC","No significant cloud"),
  NO_CLOUD_DETECTED("NCD","No cloud detected");

  private final String symbol;
  private final String description;

  CloudCoverage(String symbol, String description) {
    this.symbol = symbol;
    this.description = description;
  }

  public boolean requiresAltitude() {
    return switch (this) {
      case FEW, SCATTERED, BROKEN, OVERCAST, VERTICAL_VISIBILITY -> true;
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
