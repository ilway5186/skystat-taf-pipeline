package com.skystat.taf.domain.vo.weather.unit;

public enum TemperatureUnit implements Unit {

  CELSIUS(1.0, "C"),
  FAHRENHEIT(0.555555555, "F");

  private final double toStandardUnitFactor;
  private final String symbol;

  TemperatureUnit(double toStandardUnitFactor, String symbol) {
    this.toStandardUnitFactor = toStandardUnitFactor;
    this.symbol = symbol;
  }

  @Override
  public double toStandardUnit(double value) {
    return 0;
  }

  @Override
  public double fromStandardUnit(double value) {
    return 0;
  }
}
