package com.skystat.core.domain.vo.weather.unit;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LengthUnitTest {

  @Test
  void 길이_단위_변환이_정상동작한다() {
    assertEquals(1000.0, LengthUnit.METER.toStandardUnitValue(1000.0));
    assertEquals(1609.344, LengthUnit.SM.toStandardUnitValue(1.0), 0.000001);
    assertEquals(1.0, LengthUnit.SM.fromStandardUnitValue(1609.344), 0.000001);
  }
}
