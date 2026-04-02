package com.skystat.taf.domain.vo.weather.unit;

public enum PressureUnit implements Unit {

  HPA(1.0),
  INHG(33.86388666666667);

  public static final PressureUnit STANDARD_UNIT = HPA;

  private final double toStandardUnitFactor;

  PressureUnit(double toStandardUnitFactor) {
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

}
