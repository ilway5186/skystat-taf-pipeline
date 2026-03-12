package com.skystat.taf.domain.vo.weather.unit;

public enum TemperatureUnit implements Unit {

  CELSIUS(1.0, "C"),
  FAHRENHEIT(5.0 / 9.0, "F");

  private final double toStandardUnitFactor;
  private final String symbol;

  TemperatureUnit(double toStandardUnitFactor, String symbol) {
    this.toStandardUnitFactor = toStandardUnitFactor;
    this.symbol = symbol;
  }

  public String getSymbol() {
    return symbol;
  }

  @Override
  public double toStandardUnit(double value) {
    if (this == FAHRENHEIT) {
      return (value - 32.0) * toStandardUnitFactor;
    }

    return value * toStandardUnitFactor;
  }

  @Override
  public double fromStandardUnit(double value) {
    if (this == FAHRENHEIT) {
      return (value / toStandardUnitFactor) + 32.0;
    }

    return value / toStandardUnitFactor;
  }
}
