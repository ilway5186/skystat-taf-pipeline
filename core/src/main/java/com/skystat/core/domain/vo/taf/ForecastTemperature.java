package com.skystat.core.domain.vo.taf;

import com.skystat.core.domain.vo.weather.field.temperature.Temperature;
import com.skystat.core.domain.vo.weather.field.temperature.TemperatureExtremeType;

import java.time.Instant;
import java.util.Objects;

public record ForecastTemperature(
  TemperatureExtremeType type,
  Temperature temperature,
  Instant time
) {

  public ForecastTemperature {
    Objects.requireNonNull(type, "type cannot be null.");
    Objects.requireNonNull(temperature, "temperature cannot be null.");
    Objects.requireNonNull(time, "time cannot be null.");
  }

}
