package com.skystat.core.domain.condition;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ComparisonTest {

  @Test
  void 비교연산이_정상동작한다() {
    assertTrue(Comparison.LT.test(1, 2));
    assertTrue(Comparison.LTE.test(2, 2));
    assertTrue(Comparison.EQ.test(1.0, 1.0));
    assertTrue(Comparison.NE.test(1.0, 2.0));
    assertTrue(Comparison.GT.test(3, 2));
    assertTrue(Comparison.GTE.test(2, 2));
    assertFalse(Comparison.LT.test(2, 1));
  }
}
