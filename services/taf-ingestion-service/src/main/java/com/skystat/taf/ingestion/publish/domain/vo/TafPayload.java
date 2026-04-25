package com.skystat.taf.ingestion.publish.domain.vo;

import static com.skystat.taf.ingestion.common.validation.DomainFieldValidator.requireNonBlank;

public record TafPayload(
  String idempotencyKey,
  String icao,
  String report
) {

  public TafPayload {
    idempotencyKey = requireNonBlank(idempotencyKey, "idempotencyKey");
    icao = requireNonBlank(icao, "icao");
    report = requireNonBlank(report, "report");
  }

  public static TafPayload of(String tafId, String icao, String report) {
    return new TafPayload("TAF_PUBLISHED:" + requireNonBlank(tafId, "tafId"), icao, report);
  }

}
