package com.skystat.core.domain.vo.weather.unit;

public enum LengthUnit implements Unit {

  METER(1.0),
  FT(0.3048),
  SM(1609.344);

  public static final LengthUnit STANDARD_UNIT = METER;

  private final double toStandardUnitFactor;

  LengthUnit(double toStandardUnitFactor) {
    this.toStandardUnitFactor = toStandardUnitFactor;
  }

  @Override
  public double toStandardUnitValue(double value) {
    return value * toStandardUnitFactor;
  }

  @Override
  public double fromStandardUnitValue(double value) {
    return value / toStandardUnitFactor;
  }

  public double getToStandardUnitFactor() {
    return toStandardUnitFactor;
  }

}
