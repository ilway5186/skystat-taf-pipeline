package com.skystat.core.domain.vo.weather.unit;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PressureUnitTest {

  @Test
  void 기압_단위_변환이_정상동작한다() {
    double hpa = PressureUnit.INHG.toStandardUnitValue(1.0);
    assertEquals(33.86388666666667, hpa, 0.000001);
    assertEquals(1.0, PressureUnit.INHG.fromStandardUnitValue(hpa), 0.000001);
  }
}
