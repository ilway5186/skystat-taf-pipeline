package com.skystat.core.domain.vo.weather.unit;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TemperatureUnitTest {

  @Test
  void 섭씨와_화씨_변환이_정상동작한다() {
    assertEquals(0.0, TemperatureUnit.FAHRENHEIT.toStandardUnitValue(32.0), 0.000001);
    assertEquals(32.0, TemperatureUnit.FAHRENHEIT.fromStandardUnitValue(0.0), 0.000001);
    assertEquals(10.0, TemperatureUnit.CELSIUS.toStandardUnitValue(10.0), 0.000001);
  }
}
