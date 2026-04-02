package com.skystat.taf.domain.service.parser.field;

import com.skystat.taf.domain.service.parser.ReportRegexParser;
import com.skystat.taf.domain.vo.taf.ReportType;

import java.util.regex.Matcher;

public class ReportTypeParser extends ReportRegexParser<ReportType> {

  @Override
  public ReportType parse(String rawText) {
    Matcher matcher = matcher(rawText, ReportTypeRegex.regex());

    if (!matcher.find()) {
      return ReportType.ROUTINE;
    }

    String reportTypeGroup = extractGroup(matcher, ReportTypeRegex.TYPE);
    return ReportType.valueOf(reportTypeGroup);
  }

}
