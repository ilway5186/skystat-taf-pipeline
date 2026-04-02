package com.skystat.taf.domain.service.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class ReportRegexParser<T> implements ReportParser<T> {

  protected Matcher matcher(String rawText, String regex) {
    Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
    return pattern.matcher(rawText);
  }

  protected String extractGroup(Matcher matcher, ParsingRegex regexEnum) {
    return matcher.group(regexEnum.groupName());
  }

}
