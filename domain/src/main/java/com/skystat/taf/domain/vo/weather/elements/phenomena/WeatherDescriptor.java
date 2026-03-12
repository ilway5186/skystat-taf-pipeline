package com.skystat.taf.domain.vo.weather.elements.phenomena;

import com.skystat.taf.domain.vo.weather.elements.WeatherCode;

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

  WeatherDescriptor(String symbol, String description) {
    this.description = description;
    this.symbol = symbol;
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
