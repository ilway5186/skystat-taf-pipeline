package com.skystat.core.domain.vo.weather.field;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class IssuedTimeTest {

  @Test
  void 발행시각은_null일_수_없다() {
    assertThrows(NullPointerException.class, () -> new IssuedTime(null));
  }

  @Test
  void 정상적인_발행시각은_생성된다() {
    assertDoesNotThrow(() -> new IssuedTime(Instant.parse("2026-04-09T08:00:00Z")));
  }
}
