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
public class PublicationRequest {

  private String requestId;
  private String tafId;
  private PublicationRequestStatus status;
  private Instant requestedAt;
  private Instant publishedAt;
  private Instant failedAt;
  private String failureReason;
  private int retryCount;

  public static PublicationRequest pending(String requestId, String tafId, Instant requestedAt) {
    return new PublicationRequest(
      requireNonBlank(requestId, "requestId"),
      requireNonBlank(tafId, "tafId"),
      PublicationRequestStatus.PENDING,
      requireNonNull(requestedAt, "requestedAt"),
      null,
      null,
      null,
      0
    );
  }

  public void markSent(Instant publishedAt) {
    ensurePending();

    this.status = PublicationRequestStatus.SENT;
    this.publishedAt = requireNonNull(publishedAt, "publishedAt");
    this.failureReason = null;
  }

  public void markFailed(String failureReason, Instant failedAt) {
    ensurePending();

    this.status = PublicationRequestStatus.FAILED;
    this.failedAt = failedAt;
    this.failureReason = requireNonBlank(failureReason, "failureReason");
  }

  public void retry(Instant requestedAt) {
    if (status != PublicationRequestStatus.FAILED) {
      throw new IngestionException(IngestionErrorCode.INVALID_STATUS, "Not retryable status.");
    }

    this.status = PublicationRequestStatus.PENDING;
    this.retryCount++;
    this.requestedAt = requireNonNull(requestedAt, "requestedAt");
  }

  private void ensurePending() {
    if (status != PublicationRequestStatus.PENDING) {
      throw new IngestionException(IngestionErrorCode.INVALID_STATUS, "Not pending status.");
    }
  }

}
