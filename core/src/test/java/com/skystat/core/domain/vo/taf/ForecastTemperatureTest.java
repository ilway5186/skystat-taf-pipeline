package com.skystat.core.domain.vo.taf;

import com.skystat.core.domain.vo.weather.field.temperature.Temperature;
import com.skystat.core.domain.vo.weather.field.temperature.TemperatureExtremeType;
import com.skystat.core.domain.vo.weather.unit.TemperatureUnit;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ForecastTemperatureTest {

  @Test
  void 필수값은_null일_수_없다() {
    Temperature temperature = new Temperature(10.0, TemperatureUnit.CELSIUS);
    Instant time = Instant.parse("2026-04-09T12:00:00Z");

    assertThrows(NullPointerException.class, () -> new ForecastTemperature(null, temperature, time));
    assertThrows(NullPointerException.class, () -> new ForecastTemperature(TemperatureExtremeType.MAXIMUM, null, time));
    assertThrows(NullPointerException.class, () -> new ForecastTemperature(TemperatureExtremeType.MAXIMUM, temperature, null));
  }

  @Test
  void 정상적인_예보온도는_생성된다() {
    assertDoesNotThrow(() -> new ForecastTemperature(
      TemperatureExtremeType.MAXIMUM,
      new Temperature(10.0, TemperatureUnit.CELSIUS),
      Instant.parse("2026-04-09T12:00:00Z")
    ));
  }
}
