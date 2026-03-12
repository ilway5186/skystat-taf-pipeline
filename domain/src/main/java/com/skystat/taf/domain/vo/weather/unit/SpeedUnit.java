package com.skystat.taf.domain.vo.weather.unit;

public enum SpeedUnit implements Unit {

  MPS(1.0),
  KT(1852.0 / 3600.0);

  private final double toStandardUnitFactor;

  SpeedUnit(double toStandardUnitFactor) {
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
