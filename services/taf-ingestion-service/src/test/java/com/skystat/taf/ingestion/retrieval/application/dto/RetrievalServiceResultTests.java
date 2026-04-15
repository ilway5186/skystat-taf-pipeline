package com.skystat.taf.ingestion.retrieval.application.dto;

import com.skystat.taf.ingestion.retrieval.domain.Retrieval;
import com.skystat.taf.ingestion.retrieval.domain.RetrievalFailureReason;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RetrievalServiceResultTests {

  private static final String GROUP_ID = "group1234567890";
  private static final String ICAO = "RKSI";
  private static final Instant REQUESTED_AT = Instant.parse("2026-04-15T06:00:00Z");

  @Test
  void from은_성공한_수집결과와_원문을_다음_도메인에_전달할_형태로_변환해야한다() {
    Retrieval retrieval = Retrieval.create(GROUP_ID, ICAO, REQUESTED_AT);
    retrieval.succeed(REQUESTED_AT.plusSeconds(30));
    RetrievalResult result = RetrievalResult.success(ICAO, "TAF RKSI 150600Z ...");

    RetrievalServiceResult serviceResult = RetrievalServiceResult.from(retrieval, result);

    assertEquals(GROUP_ID, serviceResult.groupId());
    assertEquals(ICAO, serviceResult.icao());
    assertEquals("TAF RKSI 150600Z ...", serviceResult.reportText());
    assertTrue(serviceResult.isSucceeded());
    assertNull(serviceResult.failureReason());
    assertNull(serviceResult.failureDetail());
  }

  @Test
  void from은_실패한_수집결과의_실패사유를_전달해야한다() {
    Retrieval retrieval = Retrieval.create(GROUP_ID, ICAO, REQUESTED_AT);
    retrieval.fail(RetrievalFailureReason.HTTP_SERVER_ERROR, "Provider returned 500.", REQUESTED_AT.plusSeconds(30));
    RetrievalResult result = RetrievalResult.failure(
      ICAO,
      RetrievalFailureReason.HTTP_SERVER_ERROR,
      "Provider returned 500."
    );

    RetrievalServiceResult serviceResult = RetrievalServiceResult.from(retrieval, result);

    assertEquals(GROUP_ID, serviceResult.groupId());
    assertEquals(ICAO, serviceResult.icao());
    assertNull(serviceResult.reportText());
    assertTrue(serviceResult.isFailed());
    assertEquals(RetrievalFailureReason.HTTP_SERVER_ERROR, serviceResult.failureReason());
    assertEquals("Provider returned 500.", serviceResult.failureDetail());
  }

}
