package com.skystat.core.domain.vo.weather.field.temperature;

import com.skystat.core.domain.vo.weather.unit.TemperatureUnit;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TemperatureTest {

  @Test
  void 단위는_null일_수_없다() {
    assertThrows(NullPointerException.class, () -> new Temperature(10.0, null));
  }

  @Test
  void 절대영도_이하는_허용하지_않는다() {
    assertThrows(IllegalArgumentException.class, () -> new Temperature(-274.0, TemperatureUnit.CELSIUS));
    assertThrows(IllegalArgumentException.class, () -> new Temperature(-500.0, TemperatureUnit.FAHRENHEIT));
  }

  @Test
  void 정상적인_온도는_생성된다() {
    assertDoesNotThrow(() -> new Temperature(10.0, TemperatureUnit.CELSIUS));
  }
}
