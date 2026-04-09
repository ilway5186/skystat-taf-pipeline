package com.skystat.core.domain.service.parser.regex.forecast;

import com.skystat.core.domain.vo.taf.ForecastPeriod;
import com.skystat.core.exception.ParsingException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.YearMonth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ForecastPeriodParserTest {

  @Test
  void 기간을_파싱한다() {
    ForecastPeriodParser parser = ForecastPeriodParser.withYearMonth(YearMonth.of(2026, 4));

    ForecastPeriod period = parser.parse("TAF RKSI 082300Z 0900/1006");

    assertEquals(Instant.parse("2026-04-09T00:00:00Z"), period.from());
    assertEquals(Instant.parse("2026-04-10T06:00:00Z"), period.to());
  }

  @Test
  void 기간이_없으면_예외다() {
    ForecastPeriodParser parser = ForecastPeriodParser.withYearMonth(YearMonth.of(2026, 4));
    assertThrows(ParsingException.class, () -> parser.parse("TAF RKSI 082300Z"));
  }
}
