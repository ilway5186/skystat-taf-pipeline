package com.skystat.taf.ingestion.retrieval.application.dto;

import com.skystat.taf.ingestion.common.exception.IngestionErrorCode;
import com.skystat.taf.ingestion.common.exception.IngestionException;

import static com.skystat.taf.ingestion.common.validation.DomainFieldValidator.requireNonBlank;

public record GroupIdAndAttemptCount(String groupId, int attemptCount) {
  public GroupIdAndAttemptCount {
    groupId = requireNonBlank(groupId, "groupId");
    if (attemptCount <= 0) {
      throw new IngestionException(IngestionErrorCode.INVALID_INPUT, "attemptCount cannot be negative.");
    }
  }
}
