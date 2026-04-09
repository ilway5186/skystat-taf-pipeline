package com.skystat.core.domain.vo.weather.unit;

public enum TemperatureUnit implements Unit {

  CELSIUS(1.0, "C"),
  FAHRENHEIT(5.0 / 9.0, "F");

  public static final TemperatureUnit STANDARD_UNIT = CELSIUS;

  private final double toStandardUnitFactor;
  private final String symbol;

  TemperatureUnit(double toStandardUnitFactor, String symbol) {
    this.toStandardUnitFactor = toStandardUnitFactor;
    this.symbol = symbol;
  }

  public String symbol() {
    return symbol;
  }

  @Override
  public double toStandardUnitValue(double value) {
    if (this == FAHRENHEIT) {
      return (value - 32.0) * toStandardUnitFactor;
    }

    return value * toStandardUnitFactor;
  }

  @Override
  public double fromStandardUnitValue(double value) {
    if (this == FAHRENHEIT) {
      return (value / toStandardUnitFactor) + 32.0;
    }

    return value / toStandardUnitFactor;
  }
}
