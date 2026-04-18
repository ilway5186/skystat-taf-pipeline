package com.skystat.taf.ingestion.storage.domain.vo;

import java.time.Instant;

import static com.skystat.taf.ingestion.common.validation.DomainFieldValidator.requireNonBlank;
import static com.skystat.taf.ingestion.common.validation.DomainFieldValidator.requireNonNull;

public record StoredTaf(
  String tafId,
  String sourceId,
  String icao,
  String report,
  Instant issuedTime,
  Instant validFrom,
  Instant validTo,
  Instant storedAt
) {

  public StoredTaf {
    tafId = requireNonBlank(tafId, "tafId");
    sourceId = requireNonBlank(sourceId, "sourceId");
    icao = requireNonBlank(icao, "icao");
    report = requireNonBlank(report, "report");
    issuedTime = requireNonNull(issuedTime, "issuedTime");
    validFrom = requireNonNull(validFrom, "validFrom");
    validTo = requireNonNull(validTo, "validTo");
    storedAt = requireNonNull(storedAt, "storedAt");
  }

}
