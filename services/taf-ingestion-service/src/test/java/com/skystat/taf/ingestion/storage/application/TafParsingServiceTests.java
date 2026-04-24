package com.skystat.taf.ingestion.storage.application;

import com.skystat.core.domain.service.parser.TafParser;
import com.skystat.core.domain.service.parser.regex.TafRegexParser;
import com.skystat.core.domain.service.parser.regex.body.ForecastBodyParser;
import com.skystat.core.domain.service.parser.regex.forecast.*;
import com.skystat.core.domain.service.parser.regex.header.IssuedTimeParser;
import com.skystat.core.domain.service.parser.regex.header.ReportTypeParser;
import com.skystat.core.domain.service.parser.regex.header.StationIcaoParser;
import com.skystat.taf.ingestion.common.policy.IdGenerationPolicy;
import com.skystat.taf.ingestion.storage.application.dto.TafParsingResult;
import com.skystat.taf.ingestion.storage.application.service.TafParsingService;
import com.skystat.taf.ingestion.storage.domain.vo.TafParsingStatus;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

public class TafParsingServiceTests {

  IdGenerationPolicy idGenerationPolicy = () -> "generated-id";

  TafParser parser = new TafRegexParser(
    new ForecastBodyParser(
      new ChangeIndicatorParser(),
      new ForecastPeriodParser(),
      new WindParser(),
      new VisibilityParser(),
      new WeatherParser(),
      new CloudParser()
    ),
    new ReportTypeParser(),
    new IssuedTimeParser(),
    new StationIcaoParser()
  );

  @Test
  void TAF파싱에_성공하면_파싱상태는_성공이고_파싱에러원인은_null이다() {
    TafParsingService parsingService = new TafParsingServiceImpl(parser, idGenerationPolicy);
    String icao = "RKSI";
    String reportText = """
      TAF RKSI 082300Z 0900/1006 09010KT 4000 -RA FEW010 BKN030 OVC070
      TX12/0908Z TN09/0921Z TX12/1005Z
      TEMPO 0902/0908 09015G25KT 2000 RA SCT005 BKN020 OVC060
      BECMG 0909/0911 19008KT 2000 SCT005 BKN010 OVC030
      TEMPO 0914/0918 0800 -RA FG BKN005 OVC020
      BECMG 0920/0922 25010KT BR
      BECMG 1000/1002 28015G25KT 4000=
      """;
    Instant referenceTime = Instant.parse("2026-04-08T23:00:00Z");

    TafParsingResult result = parsingService.parse(icao, reportText, referenceTime);
    assertEquals(TafParsingStatus.SUCCEEDED, result.status());
    assertNull(result.failureReason());
  }

  @Test
  void TAF파싱에_성공하면_예보_발행시각과_유효시간을_알_수_있다() {
    TafParsingService parsingService = new TafParsingServiceImpl(parser, idGenerationPolicy);
    String icao = "RKSI";
    String reportText = """
      TAF RKSI 082300Z 0900/1006 09010KT 4000 -RA FEW010 BKN030 OVC070
      TX12/0908Z TN09/0921Z TX12/1005Z
      TEMPO 0902/0908 09015G25KT 2000 RA SCT005 BKN020 OVC060
      BECMG 0909/0911 19008KT 2000 SCT005 BKN010 OVC030
      TEMPO 0914/0918 0800 -RA FG BKN005 OVC020
      BECMG 0920/0922 25010KT BR
      BECMG 1000/1002 28015G25KT 4000=
      """;
    Instant referenceTime = Instant.parse("2026-04-08T23:00:00Z");
    Instant validFrom = Instant.parse("2026-04-09T00:00:00Z");
    Instant validTo = Instant.parse("2026-04-10T06:00:00Z");

    TafParsingResult result = parsingService.parse(icao, reportText, referenceTime);
    assertEquals(referenceTime, result.issuedAt());
    assertEquals(validFrom, result.validFrom());
    assertEquals(validTo, result.validTo());
  }

  @Test
  void TAF파싱에_실패하면_파싱상태는_false이고_파싱실패_원인을_알_수_있다() {
    TafParsingService parsingService = new TafParsingServiceImpl(parser, idGenerationPolicy);
    String icao = "RKSI";
    String reportText = """
      TAF RKSI 082300Z 09010KT 4000 -RA FEW010 BKN030 OVC070 TX12/0908Z TN09/0921Z TX12/1005Z
      TEMPO 0902/0908 09015G25KT 2000 RA SCT005 BKN020 OVC060
      """;
    Instant referenceTime = Instant.parse("2026-04-08T23:00:00Z");

    TafParsingResult result = parsingService.parse(icao, reportText, referenceTime);
    assertEquals(TafParsingStatus.FAILED, result.status());
    assertEquals("generated-id", result.tafId());
    assertNotNull(result.failureReason());
  }



}
