package com.skystat.taf.domain.vo.weather.unit;

public interface Unit {

  double toStandardUnitValue(double value);
  double fromStandardUnitValue(double value);

}
