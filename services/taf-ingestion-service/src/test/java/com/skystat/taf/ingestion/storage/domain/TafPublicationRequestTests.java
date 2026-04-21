package com.skystat.taf.ingestion.storage.domain;

import com.skystat.core.domain.vo.taf.TafId;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import com.skystat.taf.ingestion.common.exception.IngestionException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

public class TafPublicationRequestTests {

  @Test
  void pending_상태의_TAF발행요청_생성에_성공해야_한다() {
    String id = UUID.randomUUID().toString();
    String tafId = TafId.fromReportText("TAF RKSI 210500Z 2106/2212 ...").value();
    Instant requestedAt = Instant.now();

    TafPublicationRequest publicationRequest = TafPublicationRequest.pending(id, tafId, requestedAt);

    assertEquals(id, publicationRequest.id());
    assertEquals(tafId, publicationRequest.tafId());
    assertEquals(requestedAt, publicationRequest.requestedAt());
    assertEquals(TafPublicationRequestStatus.PENDING, publicationRequest.status());
  }

  @Test
  void pending_상태에서_발행_상태로_상태를_바꿀_수_있다() {
    String id = UUID.randomUUID().toString();
    String tafId = TafId.fromReportText("TAF RKSI 210500Z 2106/2212 ...").value();
    Instant requestedAt = Instant.now();

    TafPublicationRequest publicationRequest = TafPublicationRequest.pending(id, tafId, requestedAt);

    Instant publishedAt = Instant.now();
    publicationRequest.markSent(publishedAt);

    assertEquals(TafPublicationRequestStatus.SENT, publicationRequest.status());
    assertEquals(requestedAt, publicationRequest.requestedAt());
  }


  @Test
  void pending_상태에서_발행실패_상태로_상태를_바꿀_수_있다() {
    String id = UUID.randomUUID().toString();
    String tafId = TafId.fromReportText("TAF RKSI 210500Z 2106/2212 ...").value();
    Instant requestedAt = Instant.now();

    TafPublicationRequest publicationRequest = TafPublicationRequest.pending(id, tafId, requestedAt);

    String failureReason = "failureReason";
    Instant failedAt = Instant.now();
    publicationRequest.markFailed(failureReason, failedAt);

    assertEquals(TafPublicationRequestStatus.FAILED, publicationRequest.status());
    assertEquals(failureReason, publicationRequest.failureReason());
    assertEquals(failedAt, publicationRequest.failedAt());
  }

  @Test
  void 발행실패_상태에서_재시도할_수_있다() {
    String id = UUID.randomUUID().toString();
    String tafId = TafId.fromReportText("TAF RKSI 210500Z 2106/2212 ...").value();
    Instant requestedAt = Instant.now();

    TafPublicationRequest publicationRequest = TafPublicationRequest.pending(id, tafId, requestedAt);

    String failureReason = "failureReason";
    Instant failedAt = Instant.now();
    publicationRequest.markFailed(failureReason, failedAt);

    Instant retryAt = Instant.now();
    publicationRequest.retry(retryAt);

    assertEquals(TafPublicationRequestStatus.PENDING, publicationRequest.status());
    assertEquals(failureReason, publicationRequest.failureReason());
    assertEquals(failedAt, publicationRequest.failedAt());
    assertEquals(1, publicationRequest.retryCount());
  }

  @Test
  void pending_상태가_아니면_발행_상태로_상태를_바꿀_수_없다() {
    String id = UUID.randomUUID().toString();
    String tafId = TafId.fromReportText("TAF RKSI 210500Z 2106/2212 ...").value();
    Instant requestedAt = Instant.now();

    TafPublicationRequest publicationRequest = TafPublicationRequest.pending(id, tafId, requestedAt);

    String failureReason = "failureReason";
    Instant failedAt = Instant.now();
    publicationRequest.markFailed(failureReason, failedAt);

    assertThrows(IngestionException.class, () -> publicationRequest.markSent(Instant.now()));
  }


}
