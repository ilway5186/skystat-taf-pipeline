package com.skystat.core.domain.vo.weather.field.cloud;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CloudCoverageTest {

  @Test
  void 고도필수여부를_판단한다() {
    assertTrue(CloudCoverage.FEW.requiresAltitude());
    assertTrue(CloudCoverage.VERTICAL_VISIBILITY.requiresAltitude());
    assertFalse(CloudCoverage.CLEAR.requiresAltitude());
    assertFalse(CloudCoverage.NO_SIGNIFICANT_CLOUD.requiresAltitude());
  }

  @Test
  void 심볼로_구름피복을_찾는다() {
    assertEquals(CloudCoverage.BROKEN, CloudCoverage.fromSymbol("BKN"));
  }

  @Test
  void 알수없는_심볼은_예외다() {
    assertThrows(IllegalArgumentException.class, () -> CloudCoverage.fromSymbol("XXX"));
  }
}
