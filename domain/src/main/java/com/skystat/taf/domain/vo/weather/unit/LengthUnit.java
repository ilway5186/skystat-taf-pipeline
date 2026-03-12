package com.skystat.taf.domain.vo.weather.unit;

public enum LengthUnit implements Unit {

  METER(1.0),
  FT(0.3048),
  SM(1609.344);

  private final double toStandardUnitFactor;

  LengthUnit(double toStandardUnitFactor) {
    this.toStandardUnitFactor = toStandardUnitFactor;
  }

  @Override
  public double toStandardUnit(double value) {
    return value * toStandardUnitFactor;
  }

  @Override
  public double fromStandardUnit(double value) {
    return value / toStandardUnitFactor;
  }

}
