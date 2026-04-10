package com.skystat.core.domain.service;

import com.skystat.core.domain.service.parser.regex.body.ForecastBodyParser;
import com.skystat.core.domain.service.parser.regex.forecast.ChangeIndicatorParser;
import com.skystat.core.domain.service.parser.regex.forecast.ChangeIndicatorParser.ChangeIndicatorMatch;
import com.skystat.core.domain.service.parser.regex.forecast.CloudParser;
import com.skystat.core.domain.service.parser.regex.forecast.ForecastPeriodParser;
import com.skystat.core.domain.service.parser.regex.forecast.VisibilityParser;
import com.skystat.core.domain.service.parser.regex.forecast.WeatherParser;
import com.skystat.core.domain.service.parser.regex.forecast.WindParser;
import com.skystat.core.domain.vo.taf.ChangeIndicator;
import com.skystat.core.domain.vo.taf.ForecastBody;
import com.skystat.core.domain.vo.weather.field.cloud.Cloud;
import com.skystat.core.domain.vo.weather.field.cloud.CloudCoverage;
import com.skystat.core.domain.vo.weather.field.cloud.CloudType;
import com.skystat.core.domain.vo.weather.field.phenomena.Weather;
import com.skystat.core.domain.vo.weather.field.phenomena.WeatherDescriptor;
import com.skystat.core.domain.vo.weather.field.phenomena.WeatherIntensity;
import com.skystat.core.domain.vo.weather.field.phenomena.WeatherPhenomenon;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.skystat.core.domain.service.parser.ParserFixtures.TEST_REFERENCE_INSTANT;
import static com.skystat.core.domain.vo.taf.ChangeIndicator.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class RegexParserTests {

  String reportText = """
TAF RKSI 082300Z 0900/1006 09010KT 4000 -RA FEW010 BKN030 OVC070
TX12/0908Z TN09/0921Z TX12/1005Z
TEMPO 0902/0908 09015G25KT 2000 RA SCT005 BKN020 OVC060
BECMG 0909/0911 19008KT 2000 SCT005 BKN010 OVC030
TEMPO 0914/0918 0800 -RA FG BKN005 OVC020
BECMG 0920/0922 25010KT BR
BECMG 1000/1002 28015G25KT 4000=
""";

  ForecastBodyParser forecastBodyParser = new ForecastBodyParser(
    new ChangeIndicatorParser(),
    new ForecastPeriodParser(),
    new WindParser(),
    new VisibilityParser(),
    new WeatherParser(),
    new CloudParser()
  );

  @Test
  void TAF_지시자_파싱에_성공한다() {
    ChangeIndicatorParser indicatorParser = new ChangeIndicatorParser();
    List<ChangeIndicatorMatch> actual = indicatorParser.parse(reportText);

    assertEquals(
      List.of(TEMPO, BECMG, TEMPO, BECMG, BECMG),
      actual.stream().map(ChangeIndicatorMatch::indicator).toList()
    );
  }

  @Test
  void TAF_구름_파싱에_성공한다() {
    CloudParser cloudParser = new CloudParser();
    List<Cloud> clouds = cloudParser.parse(reportText);

    assertEquals(List.of(
      new Cloud("FEW010", CloudCoverage.FEW, 1000d, CloudType.NONE),
      new Cloud("BKN030", CloudCoverage.BROKEN, 3000d, CloudType.NONE),
      new Cloud("OVC070", CloudCoverage.OVERCAST, 7000d, CloudType.NONE),
      new Cloud("SCT005", CloudCoverage.SCATTERED, 500d, CloudType.NONE),
      new Cloud("BKN020", CloudCoverage.BROKEN, 2000d, CloudType.NONE),
      new Cloud("OVC060", CloudCoverage.OVERCAST, 6000d, CloudType.NONE),
      new Cloud("SCT005", CloudCoverage.SCATTERED, 500d, CloudType.NONE),
      new Cloud("BKN010", CloudCoverage.BROKEN, 1000d, CloudType.NONE),
      new Cloud("OVC030", CloudCoverage.OVERCAST, 3000d, CloudType.NONE),
      new Cloud("BKN005", CloudCoverage.BROKEN, 500d, CloudType.NONE),
      new Cloud("OVC020", CloudCoverage.OVERCAST, 2000d, CloudType.NONE)
    ), clouds);
  }

  @Test
  void TAF_날씨_파싱에_성공한다() {
    WeatherParser weatherParser = new WeatherParser();
    List<Weather> weathers = weatherParser.parse(reportText);

    assertEquals(List.of(
      new Weather("-RA", WeatherIntensity.LIGHT, List.of(), List.of(WeatherPhenomenon.RAIN)),
      new Weather("RA", WeatherIntensity.MODERATE, List.of(), List.of(WeatherPhenomenon.RAIN)),
      new Weather("-RA", WeatherIntensity.LIGHT, List.of(), List.of(WeatherPhenomenon.RAIN)),
      new Weather("FG", WeatherIntensity.MODERATE, List.of(), List.of(WeatherPhenomenon.FOG)),
      new Weather("BR", WeatherIntensity.MODERATE, List.of(), List.of(WeatherPhenomenon.MIST))
    ), weathers);
  }

  @Test
  void ForecastBody_구간_분리에_성공한다() {
    List<ForecastBody> forecastBodies = forecastBodyParser.parse(reportText, TEST_REFERENCE_INSTANT);

    assertEquals(
      List.of(HEADER, TEMPO, BECMG, TEMPO, BECMG, BECMG),
      forecastBodies.stream().map(ForecastBody::indicator).toList()
    );

    ForecastBody referenceBody = forecastBodies.get(2);
    ForecastBody inheritedTempoBody = forecastBodies.get(3);
    ForecastBody lastBody = forecastBodies.getLast();

    assertEquals(ChangeIndicator.BECMG, referenceBody.indicator());
    assertEquals(referenceBody.wind(), inheritedTempoBody.wind());
    assertEquals(BECMG, lastBody.indicator());
  }

  @Test
  void TEMPO는_누락된_날씨와_구름을_가장_최근_참조_필드에서_상속한다() {
    String reportText = """
TAF RKSI 082300Z 0900/1006 09010KT 4000 -RA FEW010 BKN030 OVC070
BECMG 0909/0911 19008KT 2000 BR SCT005 BKN010 OVC030
TEMPO 0914/0918 0800
      """;

    List<ForecastBody> forecastBodies = forecastBodyParser.parse(reportText, TEST_REFERENCE_INSTANT);

    ForecastBody referenceBody = forecastBodies.get(1);
    ForecastBody inheritedTempoBody = forecastBodies.get(2);

    assertEquals(referenceBody.wind(), inheritedTempoBody.wind());
    assertEquals(referenceBody.weathers(), inheritedTempoBody.weathers());
    assertEquals(referenceBody.clouds(), inheritedTempoBody.clouds());
    assertNotEquals(referenceBody.visibility(), inheritedTempoBody.visibility());
  }

  @Test
  void FM은_이전_참조_필드를_대체한다() {
    String reportText = """
TAF RKSI 082300Z 0900/1006 09010KT 4000 -RA FEW010 BKN030 OVC070
FM091900 19010KT 2000 RA BKN010
FM092100 25015KT 9999 NSW SCT030
TEMPO 0921/0923 4000 SHRA
      """;


    List<ForecastBody> forecastBodies = forecastBodyParser.parse(reportText, TEST_REFERENCE_INSTANT);

    assertEquals(
      List.of(HEADER, FM, FM, TEMPO),
      forecastBodies.stream().map(ForecastBody::indicator).toList()
    );
    assertNotEquals(forecastBodies.get(1).wind(), forecastBodies.get(2).wind());
    assertNotEquals(forecastBodies.get(1).visibility(), forecastBodies.get(2).visibility());
    assertEquals(forecastBodies.get(2).wind(), forecastBodies.get(3).wind());
    assertEquals(forecastBodies.get(2).clouds(), forecastBodies.get(3).clouds());
    assertNotEquals(forecastBodies.get(2).visibility(), forecastBodies.get(3).visibility());
  }

}
