package com.skystat.core.domain.service.parser.regex;

import com.skystat.core.domain.entity.Taf;
import com.skystat.core.domain.service.parser.TafParser;
import com.skystat.core.domain.service.parser.TemporalFieldParser;
import com.skystat.core.domain.service.parser.regex.body.ForecastBodyParser;
import com.skystat.core.domain.service.parser.regex.header.IssuedTimeParser;
import com.skystat.core.domain.service.parser.regex.header.ReportTypeParser;
import com.skystat.core.domain.service.parser.regex.header.StationIcaoParser;
import com.skystat.core.domain.vo.taf.*;
import com.skystat.core.domain.vo.weather.field.IssuedTime;
import com.skystat.core.exception.ErrorCode;
import com.skystat.core.exception.ParsingException;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.List;

@RequiredArgsConstructor
public class TafRegexParser implements TafParser {

  private final ForecastBodyParser forecastBodyParser;
  private final ReportTypeParser reportTypeParser;
  private final IssuedTimeParser issuedTimeParser;
  private final StationIcaoParser stationIcaoParser;

  @Override
  public Taf parse(String reportText, Instant referenceInstant) {
    List<ForecastBody> forecastBodies = forecastBodyParser.parse(reportText, referenceInstant);
    ForecastBody header = forecastBodies.stream()
      .filter(body -> body.indicator().equals(ChangeIndicator.HEADER))
      .findFirst()
      .orElseThrow(() -> new ParsingException(ErrorCode.INVALID_TAF_FORMAT, reportText));

    ReportType reportType = reportTypeParser.parse(header.forecastRaw());
    IssuedTime issuedTime = issuedTimeParser.parse(header.forecastRaw(), referenceInstant);
    String stationIcao = stationIcaoParser.parse(header.forecastRaw());
    ForecastPeriod validPeriod = header.period();

    TafId id = TafId.fromReportText(reportText);

    return Taf.builder()
      .tafId(id)
      .reportText(reportText)
      .reportType(reportType)
      .stationIcao(stationIcao)
      .issuedTime(issuedTime)
      .validPeriod(validPeriod)
      .body(forecastBodies)
      .temperatures(List.of())
      .build();
  }

}
