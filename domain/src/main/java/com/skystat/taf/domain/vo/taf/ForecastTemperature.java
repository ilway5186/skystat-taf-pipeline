package com.skystat.taf.domain.vo.taf;

import com.skystat.taf.domain.vo.weather.field.temperature.Temperature;
import com.skystat.taf.domain.vo.weather.field.temperature.TemperatureExtremeType;

import java.time.ZonedDateTime;
import java.util.Objects;

public record ForecastTemperature(
  TemperatureExtremeType type,
  Temperature temperature,
  ZonedDateTime time
) {

  public ForecastTemperature {
    Objects.requireNonNull(type, "type cannot be null.");
    Objects.requireNonNull(temperature, "temperature cannot be null.");
    Objects.requireNonNull(time, "time cannot be null.");
  }

}
