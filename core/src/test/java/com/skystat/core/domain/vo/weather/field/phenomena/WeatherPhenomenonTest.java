package com.skystat.core.domain.vo.weather.field.phenomena;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WeatherPhenomenonTest {

  @Test
  void 심볼로_현상을_찾는다() {
    assertEquals(WeatherPhenomenon.RAIN, WeatherPhenomenon.fromSymbol("RA"));
    assertEquals(WeatherPhenomenon.NIL_SIGNIFICANT_WEATHER, WeatherPhenomenon.fromSymbol("NSW"));
  }

  @Test
  void 알수없는_심볼은_예외다() {
    assertThrows(IllegalArgumentException.class, () -> WeatherPhenomenon.fromSymbol("XX"));
  }
}
