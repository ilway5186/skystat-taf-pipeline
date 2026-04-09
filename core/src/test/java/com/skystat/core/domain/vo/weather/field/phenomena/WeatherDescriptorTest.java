package com.skystat.core.domain.vo.weather.field.phenomena;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WeatherDescriptorTest {

  @Test
  void 심볼로_서술어를_찾는다() {
    assertEquals(WeatherDescriptor.THUNDERSTORM, WeatherDescriptor.fromSymbol("TS"));
  }

  @Test
  void 알수없는_심볼은_예외다() {
    assertThrows(IllegalArgumentException.class, () -> WeatherDescriptor.fromSymbol("XX"));
  }
}
