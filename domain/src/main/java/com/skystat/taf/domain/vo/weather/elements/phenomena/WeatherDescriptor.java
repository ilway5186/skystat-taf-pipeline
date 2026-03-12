package com.skystat.taf.domain.vo.weather.elements.phenomena;

import com.skystat.taf.domain.vo.weather.elements.WeatherCode;

public enum WeatherDescriptor implements WeatherCode {

  BC("BC","Patches"),
  BL("BL","Blowing"),
  DR("DR","Drifting"),
  DL("DL","Distant lightning"),
  FZ("FZ","Freezing"),
  MI("MI","Shallow"),
  PR("PR","Partial"),
  SH("SH","Showers"),
  TS("TS","Thunderstorm"),
  VC("VC","in the Vicinity");

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
