package com.skystat.core.domain.vo.taf;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ReportTypeTest {

  @Test
  void 빈토큰과_TAF는_ROUTINE으로_해석된다() {
    assertEquals(ReportType.ROUTINE, ReportType.from(null));
    assertEquals(ReportType.ROUTINE, ReportType.from(" "));
    assertEquals(ReportType.ROUTINE, ReportType.from("TAF"));
  }

  @Test
  void 유효한_토큰을_해석한다() {
    assertEquals(ReportType.AMD, ReportType.from("AMD"));
    assertEquals(ReportType.COR, ReportType.from("COR"));
    assertEquals(ReportType.CNL, ReportType.from("CNL"));
    assertEquals(ReportType.NIL, ReportType.from("NIL"));
  }

  @Test
  void 알수없는_토큰은_예외다() {
    assertThrows(IllegalArgumentException.class, () -> ReportType.from("INVALID"));
  }
}
