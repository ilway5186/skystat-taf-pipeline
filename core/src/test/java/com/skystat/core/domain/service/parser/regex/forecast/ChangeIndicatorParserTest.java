package com.skystat.core.domain.service.parser.regex.forecast;

import com.skystat.core.domain.vo.taf.ChangeIndicator;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.skystat.core.domain.service.parser.ParserFixtures.FULL_REPORT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChangeIndicatorParserTest {

  private final ChangeIndicatorParser parser = new ChangeIndicatorParser();

  @Test
  void 전체_전문에서_모든_지시자를_찾는다() {
    List<ChangeIndicatorParser.ChangeIndicatorMatch> matches = parser.parse(FULL_REPORT);

    assertEquals(
      List.of(ChangeIndicator.TEMPO, ChangeIndicator.BECMG, ChangeIndicator.TEMPO, ChangeIndicator.BECMG, ChangeIndicator.BECMG),
      matches.stream().map(ChangeIndicatorParser.ChangeIndicatorMatch::indicator).toList()
    );
  }

  @Test
  void FM토큰은_FM으로_정규화한다() {
    List<ChangeIndicatorParser.ChangeIndicatorMatch> matches = parser.parse("FM091900 19010KT 9999");

    assertEquals(1, matches.size());
    assertEquals(ChangeIndicator.FM, matches.getFirst().indicator());
    assertTrue(matches.getFirst().startIndex() >= 0);
  }
}
