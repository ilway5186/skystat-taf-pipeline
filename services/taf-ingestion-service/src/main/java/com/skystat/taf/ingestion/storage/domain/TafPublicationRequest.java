package com.skystat.taf.ingestion.storage.domain;

import com.skystat.taf.ingestion.common.exception.IngestionErrorCode;
import com.skystat.taf.ingestion.common.exception.IngestionException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.Instant;

import static com.skystat.taf.ingestion.common.validation.DomainFieldValidator.requireNonBlank;
import static com.skystat.taf.ingestion.common.validation.DomainFieldValidator.requireNonNull;

@Getter
@Accessors(fluent = true)
@NoArgsConstructor
@AllArgsConstructor
public class TafPublicationRequest {

  private String id;
  private String tafId;
  private TafPublicationRequestStatus status;
  private Instant requestedAt;
  private Instant publishedAt;
  private Instant failedAt;
  private String failureReason;
  private int retryCount;

  public static TafPublicationRequest pending(String id, String tafId, Instant requestedAt) {
    return new TafPublicationRequest(
      requireNonBlank(id, "id"),
      requireNonBlank(tafId, "tafId"),
      TafPublicationRequestStatus.PENDING,
      requireNonNull(requestedAt, "requestedAt"),
      null,
      null,
      null,
      0
    );
  }

  public void markSent(Instant publishedAt) {
    ensurePending();

    this.status = TafPublicationRequestStatus.SENT;
    this.publishedAt = requireNonNull(publishedAt, "publishedAt");
    this.failureReason = null;
  }

  public void markFailed(String failureReason, Instant failedAt) {
    ensurePending();

    this.status = TafPublicationRequestStatus.FAILED;
    this.failedAt = failedAt;
    this.failureReason = requireNonBlank(failureReason, "failureReason");
  }

  public void retry(Instant requestedAt) {
    if (status != TafPublicationRequestStatus.FAILED) {
      throw new IngestionException(IngestionErrorCode.INVALID_STATUS, "Not retryable status.");
    }

    this.status = TafPublicationRequestStatus.PENDING;
    this.retryCount++;
    this.requestedAt = requireNonNull(requestedAt, "requestedAt");
  }

  private void ensurePending() {
    if (status != TafPublicationRequestStatus.PENDING) {
      throw new IngestionException(IngestionErrorCode.INVALID_STATUS, "Not pending status.");
    }
  }

}
