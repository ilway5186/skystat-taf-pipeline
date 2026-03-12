package com.skystat.taf.domain.vo.taf;

import java.time.ZonedDateTime;
import java.util.Objects;

public record ForecastPeriod(ZonedDateTime from, ZonedDateTime to) {

  public ForecastPeriod {
    Objects.requireNonNull(from, "Forecast start times cannot be null.");
    Objects.requireNonNull(to, "Forecast end times cannot be null.");

    if (from.isAfter(to)) {
      throw new IllegalArgumentException("Forecast start time cannot be after the end time.");
    }
  }

  public boolean contains(ZonedDateTime time) {
    return !time.isBefore(from) && !time.isAfter(to);
  }

}
