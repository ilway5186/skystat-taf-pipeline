package com.skystat.core.domain.vo.weather.field.cloud;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CloudTypeTest {

  @Test
  void 심볼로_구름타입을_찾는다() {
    assertEquals(CloudType.CUMULONIMBUS, CloudType.fromSymbol("CB"));
    assertEquals(CloudType.NONE, CloudType.fromSymbol(""));
  }

  @Test
  void 알수없는_심볼은_예외다() {
    assertThrows(IllegalArgumentException.class, () -> CloudType.fromSymbol("XXX"));
  }
}
