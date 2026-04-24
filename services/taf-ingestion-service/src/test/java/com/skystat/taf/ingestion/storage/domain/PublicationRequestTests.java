package com.skystat.taf.ingestion.storage.domain;

import static org.junit.jupiter.api.Assertions.*;

import com.skystat.taf.ingestion.common.exception.IngestionException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

public class PublicationRequestTests {

  @Test
  void pending_상태의_TAF발행요청_생성에_성공해야_한다() {
    String id = UUID.randomUUID().toString();
    String tafId = "taf-id";
    Instant requestedAt = Instant.now();

    PublicationRequest publicationRequest = PublicationRequest.pending(id, tafId, requestedAt);

    assertEquals(id, publicationRequest.requestId());
    assertEquals(tafId, publicationRequest.tafId());
    assertEquals(requestedAt, publicationRequest.requestedAt());
    assertEquals(PublicationRequestStatus.PENDING, publicationRequest.status());
  }

  @Test
  void pending_상태에서_발행_상태로_상태를_바꿀_수_있다() {
    String id = UUID.randomUUID().toString();
    String tafId = "taf-id";
    Instant requestedAt = Instant.now();

    PublicationRequest publicationRequest = PublicationRequest.pending(id, tafId, requestedAt);

    Instant publishedAt = Instant.now();
    publicationRequest.markSent(publishedAt);

    assertEquals(PublicationRequestStatus.SENT, publicationRequest.status());
    assertEquals(requestedAt, publicationRequest.requestedAt());
  }


  @Test
  void pending_상태에서_발행실패_상태로_상태를_바꿀_수_있다() {
    String id = UUID.randomUUID().toString();
    String tafId = "taf-id";
    Instant requestedAt = Instant.now();

    PublicationRequest publicationRequest = PublicationRequest.pending(id, tafId, requestedAt);

    String failureReason = "failureReason";
    Instant failedAt = Instant.now();
    publicationRequest.markFailed(failureReason, failedAt);

    assertEquals(PublicationRequestStatus.FAILED, publicationRequest.status());
    assertEquals(failureReason, publicationRequest.failureReason());
    assertEquals(failedAt, publicationRequest.failedAt());
  }

  @Test
  void 발행실패_상태에서_재시도할_수_있다() {
    String id = UUID.randomUUID().toString();
    String tafId = "taf-id";
    Instant requestedAt = Instant.now();

    PublicationRequest publicationRequest = PublicationRequest.pending(id, tafId, requestedAt);

    String failureReason = "failureReason";
    Instant failedAt = Instant.now();
    publicationRequest.markFailed(failureReason, failedAt);

    Instant retryAt = Instant.now();
    publicationRequest.retry(retryAt);

    assertEquals(PublicationRequestStatus.PENDING, publicationRequest.status());
    assertEquals(failureReason, publicationRequest.failureReason());
    assertEquals(failedAt, publicationRequest.failedAt());
    assertEquals(1, publicationRequest.retryCount());
  }

  @Test
  void pending_상태가_아니면_발행_상태로_상태를_바꿀_수_없다() {
    String id = UUID.randomUUID().toString();
    String tafId = "taf-id";
    Instant requestedAt = Instant.now();

    PublicationRequest publicationRequest = PublicationRequest.pending(id, tafId, requestedAt);

    String failureReason = "failureReason";
    Instant failedAt = Instant.now();
    publicationRequest.markFailed(failureReason, failedAt);

    assertThrows(IngestionException.class, () -> publicationRequest.markSent(Instant.now()));
  }


}
