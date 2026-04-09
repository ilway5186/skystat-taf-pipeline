package com.skystat.core.domain.service.parser.regex.forecast;

import com.skystat.core.domain.vo.weather.field.Visibility;
import com.skystat.core.domain.vo.weather.unit.LengthUnit;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VisibilityParserTest {

  private final VisibilityParser parser = new VisibilityParser();

  @Test
  void 미터_시정을_파싱한다() {
    Optional<Visibility> visibility = parser.parse("4000 -RA");

    assertTrue(visibility.isPresent());
    assertEquals(4000.0, visibility.get().value());
    assertEquals(LengthUnit.METER, visibility.get().unit());
  }

  @Test
  void 분수_마일_시정을_파싱한다() {
    Optional<Visibility> visibility = parser.parse("1 1/2SM BR");

    assertTrue(visibility.isPresent());
    assertEquals(1.5, visibility.get().value());
    assertEquals(LengthUnit.SM, visibility.get().unit());
  }

  @Test
  void P6SM과_CAVOK을_파싱한다() {
    assertTrue(parser.parse("P6SM").orElseThrow().isP6SM());
    assertTrue(parser.parse("CAVOK").orElseThrow().isCavok());
  }

  @Test
  void 시정정보가_없으면_empty다() {
    assertTrue(parser.parse("RA BKN010").isEmpty());
  }
}
