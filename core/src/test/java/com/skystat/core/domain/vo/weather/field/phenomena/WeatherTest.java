package com.skystat.core.domain.vo.weather.field.phenomena;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WeatherTest {

  @Test
  void rawCode와_intensity는_null일_수_없다() {
    assertThrows(NullPointerException.class, () -> new Weather(null, WeatherIntensity.MODERATE, List.of(), List.of()));
    assertThrows(NullPointerException.class, () -> new Weather("RA", null, List.of(), List.of()));
  }

  @Test
  void descriptors와_phenomena가_null이면_빈리스트로_정규화된다() {
    Weather weather = new Weather("RA", WeatherIntensity.MODERATE, null, null);

    assertEquals(List.of(), weather.descriptors());
    assertEquals(List.of(), weather.phenomena());
  }

  @Test
  void 정상적인_날씨객체는_생성된다() {
    assertDoesNotThrow(() -> new Weather(
      "-TSRA",
      WeatherIntensity.LIGHT,
      List.of(WeatherDescriptor.THUNDERSTORM),
      List.of(WeatherPhenomenon.RAIN)
    ));
  }
}
