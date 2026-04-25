package com.skystat.taf.ingestion.publish.domain.vo;

import com.skystat.taf.ingestion.common.exception.IngestionException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TafPayloadTests {

  @Test
  void taf_발행_payload를_생성할_수_있다() {
    TafPayload payload = TafPayload.of("taf-id", "RKSI", "TAF RKSI 250500Z ...");

    assertEquals("TAF_PUBLISHED:taf-id", payload.idempotencyKey());
    assertEquals("RKSI", payload.icao());
    assertEquals("TAF RKSI 250500Z ...", payload.report());
  }

  @Test
  void 필수값이_비어있으면_payload를_생성할_수_없다() {
    assertAll(
      () -> assertThrows(IngestionException.class, () -> TafPayload.of("", "RKSI", "TAF RKSI 250500Z ...")),
      () -> assertThrows(IngestionException.class, () -> TafPayload.of("taf-id", "", "TAF RKSI 250500Z ...")),
      () -> assertThrows(IngestionException.class, () -> TafPayload.of("taf-id", "RKSI", "")),
      () -> assertThrows(IngestionException.class, () -> new TafPayload("", "RKSI", "TAF RKSI 250500Z ...")),
      () -> assertThrows(IngestionException.class, () -> new TafPayload("TAF_PUBLISHED:taf-id", "", "TAF RKSI 250500Z ...")),
      () -> assertThrows(IngestionException.class, () -> new TafPayload("TAF_PUBLISHED:taf-id", "RKSI", ""))
    );
  }

}
