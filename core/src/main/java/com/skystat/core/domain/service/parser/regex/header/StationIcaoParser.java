package com.skystat.core.domain.service.parser.regex.header;

import com.skystat.core.exception.ErrorCode;
import com.skystat.core.exception.InvalidInputException;
import com.skystat.core.domain.service.parser.FieldParser;
import com.skystat.core.domain.service.parser.regex.core.RegexParsingSupport;

import java.util.regex.Matcher;

public class StationIcaoParser extends RegexParsingSupport implements FieldParser<String> {

  @Override
  public String parse(String reportText) {
    Matcher matcher = matcher(reportText, StationIcaoRegex.regex());

    if (!matcher.find()) {
      throw new InvalidInputException(ErrorCode.MISSING_STATION_ICAO, reportText);
    }

    return extractGroup(matcher, StationIcaoRegex.STATION);
  }

}
