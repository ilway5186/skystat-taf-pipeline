package com.skystat.taf.ingestion.storage.domain.vo;

import java.time.Instant;

import static com.skystat.taf.ingestion.common.validation.DomainFieldValidator.requireNonBlank;
import static com.skystat.taf.ingestion.common.validation.DomainFieldValidator.requireNonNull;

public record TafStorage(
  String tafId,
  String sourceId,
  String icao,
  String report,
  Instant storedAt,
  TafParsingStatus status,
  String failureReason
) {

  public TafStorage {
    tafId = requireNonBlank(tafId, "tafId");
    sourceId = requireNonBlank(sourceId, "sourceId");
    icao = requireNonBlank(icao, "icao");
    report = requireNonBlank(report, "report");
    storedAt = requireNonNull(storedAt, "storedAt");
    status = requireNonNull(status, "status");
  }

}
