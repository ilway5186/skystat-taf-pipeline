package com.skystat.taf.domain.vo.weather.field;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Objects;

public record IssuedTime(ZonedDateTime time) {

  public IssuedTime {
    Objects.requireNonNull(time, "Issued time cannot be null.");

    if (!time.getZone().equals(ZoneOffset.UTC)) {
      throw new IllegalArgumentException("Issued time's time zone must be UTC, but: " + time.getZone() + ".");
    }
  }

}
