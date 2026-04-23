package com.skystat.taf.ingestion.storage.application;

import com.skystat.core.domain.entity.Taf;
import com.skystat.core.domain.service.parser.TafParser;
import com.skystat.core.domain.vo.taf.TafId;
import com.skystat.taf.ingestion.storage.application.dto.TafParsingResult;
import com.skystat.taf.ingestion.storage.application.service.TafParsingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class TafParsingServiceImpl implements TafParsingService {

  private final TafParser parser;

  @Override
  public TafParsingResult parse(String icao, String reportText, Instant referenceTIme) {
    String tafId = TafId.fromReportText(reportText).value();
    try {
      Taf taf = parser.parse(reportText, referenceTIme);
      return TafParsingResult.parsed(taf);
    } catch (Exception e) {
      return TafParsingResult.failed(tafId, icao, e.getMessage());
    }
  }

}
