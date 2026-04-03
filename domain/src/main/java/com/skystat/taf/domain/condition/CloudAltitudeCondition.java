package com.skystat.taf.domain.condition;

import com.skystat.taf.domain.vo.taf.ForecastBody;
import com.skystat.taf.domain.vo.weather.field.cloud.CloudCoverage;

import java.util.Objects;

public record CloudAltitudeCondition(
  CloudCoverage coverage,
  double threshold,
  Comparison comparison
) implements ForecastCondition {

  public CloudAltitudeCondition {
    Objects.requireNonNull(coverage, "coverage cannot be null.");
    Objects.requireNonNull(comparison, "comparison cannot be null.");
  }

  @Override
  public boolean matches(ForecastBody forecast) {
    return forecast.clouds().stream()
      .anyMatch(cloud ->
        cloud.coverage().equals(coverage)
          && cloud.altitude() != null
          && comparison.test(cloud.altitude(), threshold)
      );
  }
}
