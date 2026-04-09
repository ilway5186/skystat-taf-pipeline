package com.skystat.core.domain.service.parser.regex.header;

import com.skystat.core.exception.InvalidInputException;
import org.junit.jupiter.api.Test;

import static com.skystat.core.domain.service.parser.ParserFixtures.HEADER_ONLY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StationIcaoParserTest {

  private final StationIcaoParser parser = new StationIcaoParser();

  @Test
  void 관측소_ICAO를_파싱한다() {
    assertEquals("RKSI", parser.parse(HEADER_ONLY));
  }

  @Test
  void 관측소_ICAO가_없으면_예외다() {
    assertThrows(InvalidInputException.class, () -> parser.parse("TAF 082300Z 0900/1006"));
  }
}
