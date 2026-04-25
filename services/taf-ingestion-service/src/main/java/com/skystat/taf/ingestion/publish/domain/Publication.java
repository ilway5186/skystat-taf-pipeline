package com.skystat.taf.ingestion.publish.domain;

import com.skystat.taf.ingestion.common.exception.IngestionErrorCode;
import com.skystat.taf.ingestion.common.exception.IngestionException;
import com.skystat.taf.ingestion.publish.domain.vo.TafPayload;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.Instant;

import static com.skystat.taf.ingestion.common.validation.DomainFieldValidator.requireNonBlank;
import static com.skystat.taf.ingestion.common.validation.DomainFieldValidator.requireNonNull;

@Getter
@Accessors(fluent = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Publication {

  private String publicationId;
  private String tafId;
  private TafPayload payload;

  private PublicationStatus status;

  private Instant requestedAt;
  private Instant lastAttemptedAt;
  private Instant publishedAt;
  private Instant failedAt;

  private String failureReason;
  private int attemptCount;
  private int maxAttemptCount;

  public static Publication pending(String publicationId, String tafId, TafPayload payload, Instant requestedAt, int maxAttemptCount) {
    ensurePositiveMaxAttemptCount(maxAttemptCount);

    Publication pub = new Publication();
    pub.publicationId = requireNonBlank(publicationId, "publicationId");
    pub.tafId = requireNonBlank(tafId, "tafId");
    pub.payload = requireNonNull(payload, "payload");
    pub.status = PublicationStatus.PENDING;
    pub.requestedAt = requireNonNull(requestedAt, "requestedAt");
    pub.maxAttemptCount = maxAttemptCount;
    return pub;
  }

  public void retry(Instant attemptedAt) {
    if (!isFailed()) {
      throw new IngestionException(IngestionErrorCode.INVALID_STATUS, "Publication is not failed.");
    }
    if (isExhausted()) {
      throw new IngestionException(IngestionErrorCode.INVALID_STATUS, "Publish attempt count can't exceed " + maxAttemptCount);
    }

    this.lastAttemptedAt = requireNonNull(attemptedAt, "attemptedAt");
    this.status = PublicationStatus.PUBLISHING;
    this.attemptCount++;
  }

  public void markPublishing(Instant attemptedAt) {
    if (!canPublish()) {
      throw new IngestionException(IngestionErrorCode.INVALID_STATUS, "Publication is not publishable.");
    }

    this.lastAttemptedAt = requireNonNull(attemptedAt, "attemptedAt");
    this.status = PublicationStatus.PUBLISHING;
    this.attemptCount++;
  }

  public void markPublished(Instant publishedAt) {
    ensurePublishing();

    this.status = PublicationStatus.PUBLISHED;
    this.publishedAt = requireNonNull(publishedAt, "publishedAt");
    this.failureReason = null;
    this.failedAt = null;
  }

  public void markFailed(String failureReason, Instant failedAt) {
    ensurePublishing();

    this.status = PublicationStatus.FAILED;
    this.failureReason = requireNonBlank(failureReason, "failureReason");
    this.failedAt = requireNonNull(failedAt, "failedAt");
  }

  public boolean canPublish() {
    return status == PublicationStatus.PENDING && !isExhausted();
  }

  public boolean canRetry() {
    return status == PublicationStatus.FAILED && !isExhausted();
  }

  public String idempotencyKey() {
    return payload.idempotencyKey();
  }

  public boolean isPublished() {
    return status == PublicationStatus.PUBLISHED;
  }

  public boolean isFailed() {
    return status == PublicationStatus.FAILED;
  }

  public boolean isExhausted() {
    return attemptCount >= maxAttemptCount;
  }

  private void ensurePublishing() {
    if (status != PublicationStatus.PUBLISHING) {
      throw new IngestionException(IngestionErrorCode.INVALID_STATUS, "Publication is not publishing.");
    }
  }

  private static void ensurePositiveMaxAttemptCount(int maxAttemptCount) {
    if (maxAttemptCount <= 0) {
      throw new IngestionException(IngestionErrorCode.INVALID_INPUT, "maxAttemptCount must be positive.");
    }
  }

}
