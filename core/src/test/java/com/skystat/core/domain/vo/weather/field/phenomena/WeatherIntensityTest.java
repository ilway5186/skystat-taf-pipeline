package com.skystat.core.domain.vo.weather.field.phenomena;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WeatherIntensityTest {

  @Test
  void 심볼로_강도를_찾는다() {
    assertEquals(WeatherIntensity.LIGHT, WeatherIntensity.fromSymbol("-"));
    assertEquals(WeatherIntensity.MODERATE, WeatherIntensity.fromSymbol(""));
    assertEquals(WeatherIntensity.HEAVY, WeatherIntensity.fromSymbol("+"));
  }

  @Test
  void 알수없는_심볼은_예외다() {
    assertThrows(IllegalArgumentException.class, () -> WeatherIntensity.fromSymbol("*"));
  }
}
