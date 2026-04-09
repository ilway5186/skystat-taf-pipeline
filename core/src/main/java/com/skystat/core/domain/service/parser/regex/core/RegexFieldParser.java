package com.skystat.core.domain.service.parser.regex.core;

import com.skystat.core.domain.service.parser.FieldParser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class RegexFieldParser<T> implements FieldParser<T> {

  protected Matcher matcher(String reportText, String regex) {
    Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
    return pattern.matcher(reportText);
  }

  protected String extractGroup(Matcher matcher, ParsingRegex regexEnum) {
    return matcher.group(regexEnum.groupName());
  }

}
