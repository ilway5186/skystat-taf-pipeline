package com.skystat.taf.ingestion.retrieval.application;

import com.skystat.taf.ingestion.retrieval.domain.RetrievalFailureReason;

public record RetrievalResult(
  String icao,
  String reportText,
  RetrievalFailureReason failureReason,
  String failureDetail
) {

  public static RetrievalResult success(String icao, String reportText) {
    return new RetrievalResult(icao, reportText, null, null);
  }

  public static RetrievalResult failure(String icao, RetrievalFailureReason failureReason, String failureDetail) {
    return new RetrievalResult(icao, null, failureReason, failureDetail);
  }

  public boolean isSucceeded() {
    return failureReason == null;
  }

  public boolean isFailed() {
    return isSucceeded();
  }

}
