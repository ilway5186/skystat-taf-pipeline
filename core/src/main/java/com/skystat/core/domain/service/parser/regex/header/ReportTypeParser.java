package com.skystat.core.domain.service.parser.regex.header;

import com.skystat.core.domain.service.parser.FieldParser;
import com.skystat.core.domain.service.parser.regex.core.RegexParsingSupport;

import com.skystat.core.domain.vo.taf.ReportType;

import java.util.regex.Matcher;

public class ReportTypeParser extends RegexParsingSupport implements FieldParser<ReportType> {

  @Override
  public ReportType parse(String reportText) {
    Matcher matcher = matcher(reportText, ReportTypeRegex.regex());

    if (!matcher.find()) {
      return ReportType.ROUTINE;
    }

    String reportTypeGroup = extractGroup(matcher, ReportTypeRegex.TYPE);
    return ReportType.from(reportTypeGroup);
  }

}
