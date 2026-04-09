package com.skystat.core.domain.vo.weather.field;

import com.skystat.core.domain.vo.weather.unit.PressureUnit;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AltimeterTest {

  @Test
  void 단위는_null일_수_없다() {
    assertThrows(NullPointerException.class, () -> new Altimeter(1013.25, null));
  }

  @Test
  void 기압값은_음수일_수_없다() {
    assertThrows(IllegalArgumentException.class, () -> new Altimeter(-1.0, PressureUnit.HPA));
  }

  @Test
  void 정상적인_기압값은_생성된다() {
    assertDoesNotThrow(() -> new Altimeter(1013.25, PressureUnit.HPA));
  }
}
