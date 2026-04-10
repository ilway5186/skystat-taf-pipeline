package com.skystat.core.domain.service.parser.regex.forecast;

import com.skystat.core.domain.vo.taf.ForecastPeriod;
import com.skystat.core.exception.ParsingException;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static com.skystat.core.domain.service.parser.ParserFixtures.TEST_REFERENCE_INSTANT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ForecastPeriodParserTest {

  @Test
  void 기간을_파싱한다() {
    ForecastPeriodParser parser = new ForecastPeriodParser();

    ForecastPeriod period = parser.parse("TAF RKSI 082300Z 0900/1006", TEST_REFERENCE_INSTANT);

    assertEquals(Instant.parse("2026-04-09T00:00:00Z"), period.from());
    assertEquals(Instant.parse("2026-04-10T06:00:00Z"), period.to());
  }

  @Test
  void 기간이_없으면_예외다() {
    ForecastPeriodParser parser = new ForecastPeriodParser();
    assertThrows(ParsingException.class, () -> parser.parse("TAF RKSI 082300Z", TEST_REFERENCE_INSTANT));
  }
}
