package com.skystat.core.domain.vo.taf;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TafIdTest {

  @Test
  void 해시값은_소문자로_정규화된다() {
    TafId tafId = TafId.of("ABCDEF");
    assertEquals("abcdef", tafId.value());
  }

  @Test
  void 공백차이와_대소문자차이는_같은_ID를_만든다() {
    TafId first = TafId.fromReportText("TAF RKSI 082300Z 0900/1006");
    TafId second = TafId.fromReportText("  taf   rksi 082300z   0900/1006 ");

    assertEquals(first, second);
  }

  @Test
  void 내용이_다르면_다른_ID를_만든다() {
    TafId first = TafId.fromReportText("TAF RKSI 082300Z 0900/1006");
    TafId second = TafId.fromReportText("TAF RKSS 082300Z 0900/1006");

    assertNotEquals(first, second);
  }

  @Test
  void 빈값은_허용하지_않는다() {
    assertThrows(IllegalArgumentException.class, () -> TafId.of(" "));
  }
}
