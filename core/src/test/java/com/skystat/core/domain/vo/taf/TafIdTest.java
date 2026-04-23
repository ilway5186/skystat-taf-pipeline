package com.skystat.core.domain.vo.taf;

import org.junit.jupiter.api.Test;

import java.time.Instant;

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
    Instant issuedTime = Instant.parse("2026-04-08T23:00:00Z");

    TafId first = TafId.from("TAF RKSI 082300Z 0900/1006", issuedTime);
    TafId second = TafId.from("  taf   rksi 082300z   0900/1006 ", issuedTime);

    assertEquals(first, second);
  }

  @Test
  void 내용이_다르면_다른_ID를_만든다() {
    Instant issuedTime = Instant.parse("2026-04-08T23:00:00Z");

    TafId first = TafId.from("TAF RKSI 082300Z 0900/1006", issuedTime);
    TafId second = TafId.from("TAF RKSS 082300Z 0900/1006", issuedTime);

    assertNotEquals(first, second);
  }

  @Test
  void 원문이_같아도_발부시각이_다르면_다른_ID를_만든다() {
    String reportText = "TAF RKSI 082300Z 0900/1006";

    TafId first = TafId.from(reportText, Instant.parse("2026-04-08T23:00:00Z"));
    TafId second = TafId.from(reportText, Instant.parse("2026-04-09T23:00:00Z"));

    assertNotEquals(first, second);
  }

  @Test
  void 빈값은_허용하지_않는다() {
    assertThrows(IllegalArgumentException.class, () -> TafId.of(" "));
  }
}
