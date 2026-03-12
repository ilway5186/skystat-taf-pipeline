package com.skystat.taf.domain.vo.weather.unit;

public interface Unit {

  double toStandardUnit(double value);
  double fromStandardUnit(double value);

}
