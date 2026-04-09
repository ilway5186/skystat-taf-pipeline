package com.skystat.core.domain.service.parser.regex.header;

import com.skystat.core.domain.vo.taf.ReportType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReportTypeParserTest {

  private final ReportTypeParser parser = new ReportTypeParser();

  @Test
  void AMD를_파싱한다() {
    assertEquals(ReportType.AMD, parser.parse("TAF AMD RKSI 082300Z 0900/1006"));
  }

  @Test
  void 보고서_타입이_없으면_ROUTINE이다() {
    assertEquals(ReportType.ROUTINE, parser.parse("TAF RKSI 082300Z 0900/1006"));
  }
}
