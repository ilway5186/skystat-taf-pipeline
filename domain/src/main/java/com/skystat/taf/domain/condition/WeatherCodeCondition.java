package com.skystat.taf.domain.condition;

import com.skystat.taf.domain.vo.taf.ForecastBody;
import com.skystat.taf.domain.vo.weather.field.WeatherCode;

import java.util.Objects;

public record WeatherCodeCondition(WeatherCode code) implements ForecastCondition {

  public WeatherCodeCondition {
    Objects.requireNonNull(code, "code cannot be null.");
  }

  @Override
  public boolean matches(ForecastBody forecast) {
    return forecast.weathers().stream()
      .anyMatch(weather ->
        weather.descriptors().contains(code) || weather.phenomena().contains(code)
      );
  }
}
