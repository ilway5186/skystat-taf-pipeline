package com.skystat.taf.domain.condition;

import com.skystat.taf.domain.vo.taf.ForecastBody;
import com.skystat.taf.domain.vo.weather.field.Visibility;
import com.skystat.taf.domain.vo.weather.unit.LengthUnit;

import java.util.Objects;

public record VisibilityCondition(
  double threshold,
  LengthUnit unit,
  Comparison comparison
) implements ForecastCondition {

  public VisibilityCondition {
    Objects.requireNonNull(unit, "unit cannot be null.");
    Objects.requireNonNull(comparison, "comparison cannot be null.");
  }

  @Override
  public boolean matches(ForecastBody forecast) {
    Visibility visibility = forecast.visibility();
    if (visibility == null) {
      return false;
    }

    double visibilityStandardValue = visibility.unit().toStandardUnitValue(visibility.value());
    double thresholdStandardValue = unit.toStandardUnitValue(threshold);
    return comparison.test(visibilityStandardValue, thresholdStandardValue);
  }
}
