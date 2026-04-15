package com.skystat.taf.ingestion.retrieval.domain;

import com.skystat.taf.ingestion.common.exception.IngestionException;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

import static com.skystat.taf.ingestion.common.exception.IngestionErrorCode.INVALID_INPUT;
import static com.skystat.taf.ingestion.common.exception.IngestionErrorCode.INVALID_RETRIEVAL_STATE;
import static com.skystat.taf.ingestion.common.exception.IngestionErrorCode.INVALID_RETRIEVAL_TIME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RetrievalDomainTests {

  private static final String GROUP_ID = "group1234567890";
  private static final String ICAO = "RKSI";
  private static final Instant REQUESTED_AT = Instant.parse("2026-04-15T06:00:00Z");

  @Test
  void create는_수집요청_상태의_첫번째_attempt를_생성해야한다() {
    Retrieval retrieval = Retrieval.create(GROUP_ID, ICAO, REQUESTED_AT);

    assertEquals(GROUP_ID, retrieval.groupId());
    assertEquals(ICAO, retrieval.icao());
    assertEquals(REQUESTED_AT, retrieval.requestedAt());
    assertEquals(RetrievalStatus.REQUESTED, retrieval.status());
    assertEquals(1, retrieval.attemptCount());
    assertFalse(retrieval.isCompleted());
    assertFalse(retrieval.isRetryable());
    assertNull(retrieval.retrievedAt());
    assertNull(retrieval.failureReason());
    assertNull(retrieval.failureDetail());
  }

  @Test
  void succeed는_REQUESTED_상태를_SUCCEEDED로_완료해야한다() {
    Retrieval retrieval = Retrieval.create(GROUP_ID, ICAO, REQUESTED_AT);
    Instant retrievedAt = REQUESTED_AT.plusSeconds(30);

    retrieval.succeed(retrievedAt);

    assertEquals(RetrievalStatus.SUCCEEDED, retrieval.status());
    assertEquals(retrievedAt, retrieval.retrievedAt());
    assertTrue(retrieval.isSucceeded());
    assertTrue(retrieval.isCompleted());
    assertFalse(retrieval.isFailed());
    assertFalse(retrieval.isRetryable());
    assertEquals(Duration.ofSeconds(30), retrieval.duration());
    assertNull(retrieval.failureReason());
    assertNull(retrieval.failureDetail());
  }

  @Test
  void fail은_REQUESTED_상태를_FAILED로_완료하고_실패사유를_기록해야한다() {
    Retrieval retrieval = Retrieval.create(GROUP_ID, ICAO, REQUESTED_AT);
    Instant retrievedAt = REQUESTED_AT.plusSeconds(45);

    retrieval.fail(RetrievalFailureReason.HTTP_SERVER_ERROR, "Provider returned 500.", retrievedAt);

    assertEquals(RetrievalStatus.FAILED, retrieval.status());
    assertEquals(RetrievalFailureReason.HTTP_SERVER_ERROR, retrieval.failureReason());
    assertEquals("Provider returned 500.", retrieval.failureDetail());
    assertEquals(retrievedAt, retrieval.retrievedAt());
    assertTrue(retrieval.isFailed());
    assertTrue(retrieval.isCompleted());
    assertTrue(retrieval.isRetryable());
    assertEquals(Duration.ofSeconds(45), retrieval.duration());
  }

  @Test
  void nextAttempt는_retryable_실패건의_groupId와_icao를_유지하고_attempt를_증가시켜야한다() {
    Retrieval retrieval = Retrieval.create(GROUP_ID, ICAO, REQUESTED_AT);
    retrieval.fail(RetrievalFailureReason.HTTP_SERVER_ERROR, "Provider returned 500.", REQUESTED_AT.plusSeconds(10));
    Instant retryRequestedAt = REQUESTED_AT.plusSeconds(60);

    Retrieval retryRetrieval = retrieval.nextAttempt(retryRequestedAt);

    assertEquals(GROUP_ID, retryRetrieval.groupId());
    assertEquals(ICAO, retryRetrieval.icao());
    assertEquals(retryRequestedAt, retryRetrieval.requestedAt());
    assertEquals(RetrievalStatus.REQUESTED, retryRetrieval.status());
    assertEquals(2, retryRetrieval.attemptCount());
    assertNull(retryRetrieval.retrievedAt());
    assertNull(retryRetrieval.failureReason());
    assertNull(retryRetrieval.failureDetail());
  }

  @Test
  void nextAttempt는_retryable이_아닌_수집건이면_거부해야한다() {
    Retrieval retrieval = Retrieval.create(GROUP_ID, ICAO, REQUESTED_AT);
    retrieval.fail(RetrievalFailureReason.HTTP_CLIENT_ERROR, "Bad request.", REQUESTED_AT.plusSeconds(10));

    IngestionException exception = assertThrows(
      IngestionException.class,
      () -> retrieval.nextAttempt(REQUESTED_AT.plusSeconds(60))
    );

    assertEquals(INVALID_RETRIEVAL_STATE, exception.errorCode());
  }

  @Test
  void 완료된_수집건은_다시_완료할_수_없다() {
    Retrieval retrieval = Retrieval.create(GROUP_ID, ICAO, REQUESTED_AT);
    retrieval.succeed(REQUESTED_AT.plusSeconds(10));

    IngestionException exception = assertThrows(
      IngestionException.class,
      () -> retrieval.fail(RetrievalFailureReason.HTTP_SERVER_ERROR, "Provider returned 500.", REQUESTED_AT.plusSeconds(20))
    );

    assertEquals(INVALID_RETRIEVAL_STATE, exception.errorCode());
  }

  @Test
  void retrievedAt은_requestedAt보다_이전일_수_없다() {
    Retrieval retrieval = Retrieval.create(GROUP_ID, ICAO, REQUESTED_AT);

    IngestionException exception = assertThrows(
      IngestionException.class,
      () -> retrieval.succeed(REQUESTED_AT.minusSeconds(1))
    );

    assertEquals(INVALID_RETRIEVAL_TIME, exception.errorCode());
  }

  @Test
  void duration은_완료된_수집건에서만_계산할_수_있다() {
    Retrieval retrieval = Retrieval.create(GROUP_ID, ICAO, REQUESTED_AT);

    IngestionException exception = assertThrows(IngestionException.class, retrieval::duration);

    assertEquals(INVALID_RETRIEVAL_STATE, exception.errorCode());
  }

  @Test
  void create는_필수값이_비어있으면_거부해야한다() {
    IngestionException groupIdException = assertThrows(
      IngestionException.class,
      () -> Retrieval.create(" ", ICAO, REQUESTED_AT)
    );
    IngestionException icaoException = assertThrows(
      IngestionException.class,
      () -> Retrieval.create(GROUP_ID, " ", REQUESTED_AT)
    );
    IngestionException requestedAtException = assertThrows(
      IngestionException.class,
      () -> Retrieval.create(GROUP_ID, ICAO, null)
    );

    assertEquals(INVALID_INPUT, groupIdException.errorCode());
    assertEquals(INVALID_INPUT, icaoException.errorCode());
    assertEquals(INVALID_INPUT, requestedAtException.errorCode());
  }

}
