package com.skystat.core.domain.vo.weather.field;

import java.time.Instant;
import java.util.Objects;

public record IssuedTime(Instant time) {

  public IssuedTime {
    Objects.requireNonNull(time, "Issued time cannot be null.");
  }

}
