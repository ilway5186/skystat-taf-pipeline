package com.skystat.core.domain.vo.weather.field.cloud;

import com.skystat.core.domain.vo.weather.field.WeatherCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
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

  public boolean requiresAltitude() {
    return switch (this) {
      case FEW, SCATTERED, BROKEN, OVERCAST, VERTICAL_VISIBILITY -> true;
      default -> false;
    };
  }

  public static CloudCoverage fromSymbol(String symbol) {
    for (CloudCoverage coverage : values()) {
      if (coverage.symbol.equals(symbol)) {
        return coverage;
      }
    }
    throw new IllegalArgumentException("Unknown cloud coverage symbol: " + symbol);
  }

}
