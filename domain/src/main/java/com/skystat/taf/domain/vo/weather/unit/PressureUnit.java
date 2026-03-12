package com.skystat.taf.domain.vo.weather.unit;

public enum PressureUnit implements Unit {

  HPA(1.0),
  INHG(33.86388666666667);

  private final double toStandardUnitFactor;

  PressureUnit(double toStandardUnitFactor) {
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
