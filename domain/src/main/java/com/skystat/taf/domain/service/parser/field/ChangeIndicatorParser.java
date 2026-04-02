package com.skystat.taf.domain.service.parser.field;

import com.skystat.taf.domain.service.parser.ReportRegexParser;
import com.skystat.taf.domain.vo.taf.ChangeIndicator;

import java.util.regex.Matcher;

public class ChangeIndicatorParser extends ReportRegexParser<ChangeIndicator> {

  @Override
  public ChangeIndicator parse(String rawText) {
    Matcher matcher = matcher(rawText, ChangeIndicatorRegex.regex());

    if (!matcher.find()) {
      return ChangeIndicator.HEADER;
    }

    String indicatorGroup = extractGroup(matcher, ChangeIndicatorRegex.INDICATOR);
    return ChangeIndicator.from(indicatorGroup);
  }

}
