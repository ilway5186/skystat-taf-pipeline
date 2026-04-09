package com.skystat.core.domain.vo.taf;

import java.time.Instant;
import java.util.Objects;

public record ForecastPeriod(Instant from, Instant to) {

  public ForecastPeriod {
    Objects.requireNonNull(from, "Forecast start times cannot be null.");
    Objects.requireNonNull(to, "Forecast end times cannot be null.");

    if (from.isAfter(to)) {
      throw new IllegalArgumentException("Forecast start time cannot be after the end time.");
    }
  }

  public boolean contains(Instant time) {
    return !time.isBefore(from) && !time.isAfter(to);
  }

  public boolean contains(ForecastPeriod other) {
    Objects.requireNonNull(other, "other forecast period cannot be null.");
    return contains(other.from()) && contains(other.to());
  }

}
