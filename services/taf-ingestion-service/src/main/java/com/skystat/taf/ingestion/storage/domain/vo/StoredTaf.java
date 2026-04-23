package com.skystat.taf.ingestion.storage.domain.vo;

import com.skystat.core.domain.entity.Taf;

import java.time.Instant;

import static com.skystat.taf.ingestion.common.validation.DomainFieldValidator.requireNonBlank;
import static com.skystat.taf.ingestion.common.validation.DomainFieldValidator.requireNonNull;

public record StoredTaf(
  String tafId,
  String sourceId,
  String icao,
  String report,
  Instant storedAt,
  TafParsingStatus status,
  String failureReason
) {

  public StoredTaf {
    tafId = requireNonBlank(tafId, "tafId");
    sourceId = requireNonBlank(sourceId, "sourceId");
    icao = requireNonBlank(icao, "icao");
    report = requireNonBlank(report, "report");
    storedAt = requireNonNull(storedAt, "storedAt");
    status = requireNonNull(status, "status");
  }

}
