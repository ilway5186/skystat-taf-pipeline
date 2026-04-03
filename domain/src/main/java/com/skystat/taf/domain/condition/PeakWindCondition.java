package com.skystat.taf.domain.condition;

import com.skystat.taf.domain.vo.taf.ForecastBody;
import com.skystat.taf.domain.vo.weather.field.wind.Wind;
import com.skystat.taf.domain.vo.weather.unit.SpeedUnit;

import java.util.Objects;

public record PeakWindCondition(
  double threshold,
  SpeedUnit unit,
  Comparison comparison
) implements ForecastCondition {

  public PeakWindCondition {
    Objects.requireNonNull(unit, "unit cannot be null.");
    Objects.requireNonNull(comparison, "comparison cannot be null.");
  }

  @Override
  public boolean matches(ForecastBody forecast) {
    Wind wind = forecast.wind();
    if (wind == null || wind.peakSpeed() == null) {
      return false;
    }

    double peakWindStandardValue = wind.unit().toStandardUnitValue(wind.peakSpeed());
    double thresholdStandardValue = unit.toStandardUnitValue(threshold);
    return comparison.test(peakWindStandardValue, thresholdStandardValue);
  }
}
