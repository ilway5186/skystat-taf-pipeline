package com.skystat.core.domain.service.parser.regex;

import com.skystat.core.domain.entity.Taf;
import com.skystat.core.domain.service.parser.regex.body.ForecastBodyParser;
import com.skystat.core.domain.service.parser.regex.forecast.ChangeIndicatorParser;
import com.skystat.core.domain.service.parser.regex.forecast.CloudParser;
import com.skystat.core.domain.service.parser.regex.forecast.ForecastPeriodParser;
import com.skystat.core.domain.service.parser.regex.forecast.VisibilityParser;
import com.skystat.core.domain.service.parser.regex.forecast.WeatherParser;
import com.skystat.core.domain.service.parser.regex.forecast.WindParser;
import com.skystat.core.domain.service.parser.regex.header.IssuedTimeParser;
import com.skystat.core.domain.service.parser.regex.header.ReportTypeParser;
import com.skystat.core.domain.service.parser.regex.header.StationIcaoParser;
import org.junit.jupiter.api.Test;

import static com.skystat.core.domain.service.parser.ParserFixtures.FULL_REPORT;
import static com.skystat.core.domain.service.parser.ParserFixtures.TEST_REFERENCE_INSTANT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TafRegexParserTest {

  private final TafRegexParser parser = new TafRegexParser(
    new ForecastBodyParser(
      new ChangeIndicatorParser(),
      new ForecastPeriodParser(),
      new WindParser(),
      new VisibilityParser(),
      new WeatherParser(),
      new CloudParser()
    ),
    new ReportTypeParser(),
    new IssuedTimeParser(),
    new StationIcaoParser()
  );

  @Test
  void 전문을_Taf_엔티티로_파싱한다() {
    Taf taf = parser.parse(FULL_REPORT, TEST_REFERENCE_INSTANT);

    assertNotNull(taf);
    assertEquals(6, taf.body().size());
    assertEquals(taf.validPeriod(), taf.header().period());
  }
}
