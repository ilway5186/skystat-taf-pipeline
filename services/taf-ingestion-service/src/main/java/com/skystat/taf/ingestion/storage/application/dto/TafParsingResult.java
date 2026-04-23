package com.skystat.taf.ingestion.storage.application.dto;

import com.skystat.core.domain.entity.Taf;
import com.skystat.taf.ingestion.storage.domain.vo.TafParsingStatus;

import java.time.Instant;

public record TafParsingResult(
  String tafId,
  String icao,
  TafParsingStatus status,
  String failureReason,
  Instant issuedAt,
  Instant validFrom,
  Instant validTo
) {

  public static TafParsingResult parsed(Taf taf) {
    return new TafParsingResult(
      taf.tafId().value(),
      taf.stationIcao(),
      TafParsingStatus.SUCCEEDED,
      null,
      taf.issuedTime().time(),
      taf.validPeriod().from(),
      taf.validPeriod().to()
    );
  }

  public static TafParsingResult failed(String tafId, String icao, String failureReason) {
    return new TafParsingResult(
      tafId,
      icao,
      TafParsingStatus.FAILED,
      failureReason,
      null,
      null,
      null
    );
  }

}
