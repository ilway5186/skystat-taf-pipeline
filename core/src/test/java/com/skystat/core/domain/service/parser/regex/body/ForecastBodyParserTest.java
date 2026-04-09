package com.skystat.core.domain.service.parser.regex.body;

import com.skystat.core.domain.service.parser.regex.forecast.ChangeIndicatorParser;
import com.skystat.core.domain.service.parser.regex.forecast.CloudParser;
import com.skystat.core.domain.service.parser.regex.forecast.ForecastPeriodParser;
import com.skystat.core.domain.service.parser.regex.forecast.VisibilityParser;
import com.skystat.core.domain.service.parser.regex.forecast.WeatherParser;
import com.skystat.core.domain.service.parser.regex.forecast.WindParser;
import com.skystat.core.domain.vo.taf.ChangeIndicator;
import com.skystat.core.domain.vo.taf.ForecastBody;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.skystat.core.domain.service.parser.ParserFixtures.FULL_REPORT;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ForecastBodyParserTest {

  private final ForecastBodyParser parser = new ForecastBodyParser(
    new ChangeIndicatorParser(),
    new ForecastPeriodParser(),
    new WindParser(),
    new VisibilityParser(),
    new WeatherParser(),
    new CloudParser()
  );

  @Test
  void 전체_전문을_forecast_body_목록으로_파싱한다() {
    List<ForecastBody> bodies = parser.parse(FULL_REPORT);

    assertEquals(
      List.of(ChangeIndicator.HEADER, ChangeIndicator.TEMPO, ChangeIndicator.BECMG, ChangeIndicator.TEMPO, ChangeIndicator.BECMG, ChangeIndicator.BECMG),
      bodies.stream().map(ForecastBody::indicator).toList()
    );
  }

  @Test
  void TEMPO는_직전_referenceable_body를_상속한다() {
    String reportText = """
TAF RKSI 082300Z 0900/1006 09010KT 4000 -RA FEW010 BKN030 OVC070
BECMG 0909/0911 19008KT 2000 BR SCT005 BKN010 OVC030
TEMPO 0914/0918 0800
""";

    List<ForecastBody> bodies = parser.parse(reportText);

    assertEquals(bodies.get(1).wind(), bodies.get(2).wind());
    assertEquals(bodies.get(1).weathers(), bodies.get(2).weathers());
    assertEquals(bodies.get(1).clouds(), bodies.get(2).clouds());
  }

  @Test
  void FM은_이전_reference를_대체한다() {
    String reportText = """
TAF RKSI 082300Z 0900/1006 09010KT 4000 -RA FEW010 BKN030 OVC070
FM091900 19010KT 2000 RA BKN010
FM092100 25015KT 9999 NSW SCT030
TEMPO 0921/0923 4000 SHRA
""";

    List<ForecastBody> bodies = parser.parse(reportText);

    assertEquals(bodies.get(2).wind(), bodies.get(3).wind());
    assertEquals(bodies.get(2).clouds(), bodies.get(3).clouds());
  }
}
