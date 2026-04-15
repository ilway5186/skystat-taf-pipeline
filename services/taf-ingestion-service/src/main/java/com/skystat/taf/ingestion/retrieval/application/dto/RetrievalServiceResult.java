package com.skystat.taf.ingestion.retrieval.application.dto;

import com.skystat.taf.ingestion.retrieval.domain.Retrieval;
import com.skystat.taf.ingestion.retrieval.domain.RetrievalFailureReason;

public record RetrievalServiceResult(
  String groupId,
  String icao,
  String reportText,
  RetrievalFailureReason failureReason,
  String failureDetail
) {

  public static RetrievalServiceResult from(Retrieval retrieval, RetrievalResult result) {
    return new RetrievalServiceResult(
      retrieval.groupId(),
      retrieval.icao(),
      result.reportText(),
      result.failureReason(),
      result.failureDetail()
    );
  }

  public boolean isSucceeded() {
    return failureReason == null;
  }

  public boolean isFailed() {
    return !isSucceeded();
  }

}
