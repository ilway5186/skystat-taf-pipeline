package com.skystat.core.domain.service.parser.regex.body;

import com.skystat.core.domain.service.parser.regex.forecast.CloudParser;
import com.skystat.core.domain.service.parser.regex.forecast.ForecastPeriodParser;
import com.skystat.core.domain.service.parser.regex.forecast.VisibilityParser;
import com.skystat.core.domain.service.parser.regex.forecast.WeatherParser;
import com.skystat.core.domain.service.parser.regex.forecast.WindParser;
import com.skystat.core.domain.vo.taf.ChangeIndicator;
import org.junit.jupiter.api.Test;

import java.time.YearMonth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ForecastSectionParserTest {

  private final ForecastSectionParser parser = new ForecastSectionParser(
    ForecastPeriodParser.withYearMonth(YearMonth.of(2026, 4)),
    new WindParser(),
    new VisibilityParser(),
    new WeatherParser(),
    new CloudParser()
  );

  @Test
  void section에서_명시된_필드들을_파싱한다() {
    ParsedForecastSection section = parser.parse(new ForecastSection(
      ChangeIndicator.TEMPO,
      "TEMPO 0902/0908 09015G25KT 2000 RA SCT005 BKN020 OVC060"
    ));

    assertEquals(ChangeIndicator.TEMPO, section.indicator());
    assertTrue(section.wind().isPresent());
    assertTrue(section.visibility().isPresent());
    assertEquals(1, section.weathers().size());
    assertEquals(3, section.clouds().size());
  }
}
