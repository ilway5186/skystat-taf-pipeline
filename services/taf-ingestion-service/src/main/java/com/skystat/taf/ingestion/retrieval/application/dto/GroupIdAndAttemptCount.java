package com.skystat.taf.ingestion.retrieval.application.dto;

import com.skystat.taf.ingestion.common.exception.IngestionErrorCode;
import com.skystat.taf.ingestion.common.exception.IngestionException;

public record GroupIdAndAttemptCount(String groupId, int attemptCount) {
  public GroupIdAndAttemptCount {
    if (groupId == null || groupId.isBlank()) {
      throw new IngestionException(IngestionErrorCode.INVALID_INPUT, "groupId cannot be null or blank.");
    }
    if (attemptCount <= 0) {
      throw new IngestionException(IngestionErrorCode.INVALID_INPUT, "attemptCount cannot be negative.");
    }
  }
}
