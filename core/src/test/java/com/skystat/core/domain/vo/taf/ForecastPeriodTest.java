package com.skystat.core.domain.vo.taf;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ForecastPeriodTest {

  @Test
  void 시작시간은_종료시간보다_늦을_수_없다() {
    assertThrows(IllegalArgumentException.class, () -> new ForecastPeriod(
      Instant.parse("2026-04-10T06:00:01Z"),
      Instant.parse("2026-04-10T06:00:00Z")
    ));
  }

  @Test
  void 특정_시각_포함여부를_판단한다() {
    ForecastPeriod period = new ForecastPeriod(
      Instant.parse("2026-04-09T09:00:00Z"),
      Instant.parse("2026-04-10T06:00:00Z")
    );

    assertTrue(period.contains(Instant.parse("2026-04-09T09:00:00Z")));
    assertTrue(period.contains(Instant.parse("2026-04-10T06:00:00Z")));
    assertFalse(period.contains(Instant.parse("2026-04-10T06:00:01Z")));
  }

  @Test
  void 다른_기간_포함여부를_판단한다() {
    ForecastPeriod period = new ForecastPeriod(
      Instant.parse("2026-04-09T09:00:00Z"),
      Instant.parse("2026-04-10T06:00:00Z")
    );
    ForecastPeriod inside = new ForecastPeriod(
      Instant.parse("2026-04-09T10:00:00Z"),
      Instant.parse("2026-04-09T11:00:00Z")
    );
    ForecastPeriod outside = new ForecastPeriod(
      Instant.parse("2026-04-09T08:59:59Z"),
      Instant.parse("2026-04-09T11:00:00Z")
    );

    assertTrue(period.contains(inside));
    assertFalse(period.contains(outside));
  }
}
