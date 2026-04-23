package com.skystat.taf.ingestion.storage.application.service;

import com.skystat.taf.ingestion.storage.application.dto.TafParsingResult;

import java.time.Instant;

public interface TafParsingService {
  TafParsingResult parse(String icao, String reportText, Instant referenceTIme);
}
