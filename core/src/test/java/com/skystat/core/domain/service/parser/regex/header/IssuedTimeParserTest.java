package com.skystat.core.domain.service.parser.regex.header;

import com.skystat.core.domain.vo.weather.field.IssuedTime;
import com.skystat.core.exception.ParsingException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.YearMonth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class IssuedTimeParserTest {

  @Test
  void 발행시각을_파싱한다() {
    IssuedTimeParser parser = IssuedTimeParser.withYearMonth(YearMonth.of(2026, 4));

    IssuedTime issuedTime = parser.parse("TAF RKSI 082300Z 0900/1006");

    assertEquals(Instant.parse("2026-04-08T23:00:00Z"), issuedTime.time());
  }

  @Test
  void 발행시각이_없으면_예외다() {
    IssuedTimeParser parser = IssuedTimeParser.withYearMonth(YearMonth.of(2026, 4));

    assertThrows(ParsingException.class, () -> parser.parse("TAF RKSI 0900/1006"));
  }
}
