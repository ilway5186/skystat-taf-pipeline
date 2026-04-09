package com.skystat.core.domain.vo.weather.unit;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SpeedUnitTest {

  @Test
  void 속도_단위_변환이_정상동작한다() {
    double meterPerSecond = SpeedUnit.KT.toStandardUnitValue(1.0);
    assertEquals(1852.0 / 3600.0, meterPerSecond, 0.000001);
    assertEquals(1.0, SpeedUnit.KT.fromStandardUnitValue(meterPerSecond), 0.000001);
  }
}
