package com.skystat.core.domain.service.parser.regex.header;

import com.skystat.core.exception.ErrorCode;
import com.skystat.core.exception.InvalidInputException;
import com.skystat.core.domain.service.parser.regex.core.RegexFieldParser;

import java.util.regex.Matcher;

public class StationIcaoParser extends RegexFieldParser<String> {

  @Override
  public String parse(String reportText) {
    Matcher matcher = matcher(reportText, StationIcaoRegex.regex());

    if (!matcher.find()) {
      throw new InvalidInputException(ErrorCode.MISSING_STATION_ICAO, reportText);
    }

    return extractGroup(matcher, StationIcaoRegex.STATION);
  }

}
