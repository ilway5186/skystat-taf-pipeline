package com.skystat.taf.ingestion.retrieval.domain;

import com.skystat.taf.ingestion.common.exception.IngestionException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.Duration;
import java.time.Instant;

import static com.skystat.taf.ingestion.common.exception.IngestionErrorCode.*;

@Getter
@Accessors(fluent = true)
@NoArgsConstructor
@AllArgsConstructor
public class Retrieval {

  private String groupId;
  private String icao;
  private String reportText;

  private Instant requestedAt;
  private Instant retrievedAt;

  private RetrievalStatus status;
  private RetrievalFailureReason failureReason;
  private String failureDetail;

  private int attemptCount;

  public static Retrieval create(String groupId, String stationIcao, Instant requestedAt) {
    Retrieval retrieval = new Retrieval();
    retrieval.groupId = requireNonBlank(groupId, "id");
    retrieval.icao = requireNonBlank(stationIcao, "stationIcao");
    retrieval.requestedAt = requireNonNull(requestedAt, "requestedAt");
    retrieval.status = RetrievalStatus.REQUESTED;
    retrieval.attemptCount = 1;
    return retrieval;
  }

  public void succeed(String reportText, Instant retrievedAt) {
    ensureRequested();
    ensureValidRetrievedAt(retrievedAt);

    this.reportText = requireNonBlank(reportText, "reportText");
    this.retrievedAt = retrievedAt;
    this.status = RetrievalStatus.SUCCEEDED;
    this.failureReason = null;
    this.failureDetail = null;
  }

  public void fail(RetrievalFailureReason failureReason, String failureDetail, Instant retrievedAt) {
    ensureRequested();
    ensureValidRetrievedAt(retrievedAt);

    this.failureReason = requireNonNull(failureReason, "failureReason");
    this.failureDetail = failureDetail;
    this.retrievedAt = retrievedAt;
    this.status = RetrievalStatus.FAILED;
    this.reportText = null;
  }

  public Retrieval nextAttempt(Instant requestedAt) {
    if (!isRetryable()) {
      throw new IngestionException(INVALID_RETRIEVAL_STATE, "Retrieval is not retryable.");
    }

    Retrieval retrieval = new Retrieval();
    retrieval.groupId = groupId;
    retrieval.icao = icao;
    retrieval.requestedAt = requireNonNull(requestedAt, "requestedAt");
    retrieval.status = RetrievalStatus.REQUESTED;
    retrieval.attemptCount = attemptCount + 1;
    return retrieval;
  }

  public boolean isSucceeded() {
    return status == RetrievalStatus.SUCCEEDED;
  }

  public boolean isFailed() {
    return status == RetrievalStatus.FAILED;
  }

  public boolean isCompleted() {
    return isSucceeded() || isFailed();
  }

  public boolean isRetryable() {
    return isFailed() && failureReason.retryable();
  }

  public Duration duration() {
    if (retrievedAt == null) {
      throw new IngestionException(INVALID_RETRIEVAL_STATE, "Retrieval has not been completed.");
    }
    return Duration.between(requestedAt, retrievedAt);
  }

  private void ensureRequested() {
    if (status != RetrievalStatus.REQUESTED) {
      throw new IngestionException(INVALID_RETRIEVAL_STATE, "Retrieval is already completed.");
    }
  }

  private void ensureValidRetrievedAt(Instant retrievedAt) {
    requireNonNull(retrievedAt, "retrievedAt");
    if (retrievedAt.isBefore(requestedAt)) {
      throw new IngestionException(INVALID_RETRIEVAL_TIME, "Retrieved time cannot be before requested time.");
    }
  }

  private static String requireNonBlank(String value, String fieldName) {
    if (value == null || value.isBlank()) {
      throw new IngestionException(INVALID_INPUT, fieldName + " cannot be blank.");
    }
    return value;
  }

  private static <T> T requireNonNull(T value, String fieldName) {
    if (value == null) {
      throw new IngestionException(INVALID_INPUT, fieldName + " cannot be null.");
    }
    return value;
  }

}
