package com.skystat.core.domain.service.parser.regex.header;

import com.skystat.core.domain.vo.weather.field.IssuedTime;
import com.skystat.core.exception.ParsingException;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static com.skystat.core.domain.service.parser.ParserFixtures.TEST_REFERENCE_INSTANT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class IssuedTimeParserTest {

  @Test
  void 발행시각을_파싱한다() {
    IssuedTimeParser parser = new IssuedTimeParser();

    IssuedTime issuedTime = parser.parse("TAF RKSI 082300Z 0900/1006", TEST_REFERENCE_INSTANT);

    assertEquals(Instant.parse("2026-04-08T23:00:00Z"), issuedTime.time());
  }

  @Test
  void 발행시각이_없으면_예외다() {
    IssuedTimeParser parser = new IssuedTimeParser();

    assertThrows(ParsingException.class, () -> parser.parse("TAF RKSI 0900/1006", TEST_REFERENCE_INSTANT));
  }
}
