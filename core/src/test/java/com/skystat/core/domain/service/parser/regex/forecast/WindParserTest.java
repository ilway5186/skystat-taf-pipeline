package com.skystat.core.domain.service.parser.regex.forecast;

import com.skystat.core.domain.vo.weather.field.wind.Wind;
import com.skystat.core.domain.vo.weather.field.wind.WindDirectionType;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WindParserTest {

  private final WindParser parser = new WindParser();

  @Test
  void 고정풍향과_돌풍을_파싱한다() {
    Optional<Wind> wind = parser.parse("09015G25KT");

    assertTrue(wind.isPresent());
    assertEquals(WindDirectionType.FIXED, wind.get().direction().type());
    assertEquals(90.0, wind.get().direction().degree());
    assertEquals(15.0, wind.get().speed());
    assertEquals(25.0, wind.get().gust());
  }

  @Test
  void 가변풍향을_파싱한다() {
    Optional<Wind> wind = parser.parse("VRB03KT");

    assertTrue(wind.isPresent());
    assertEquals(WindDirectionType.VARIABLE, wind.get().direction().type());
    assertEquals(3.0, wind.get().speed());
  }

  @Test
  void 풍향정보가_없으면_empty다() {
    assertTrue(parser.parse("9999 BR").isEmpty());
  }
}
