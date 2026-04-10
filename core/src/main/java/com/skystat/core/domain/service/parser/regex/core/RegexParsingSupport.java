package com.skystat.core.domain.service.parser.regex.core;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexParsingSupport {

  protected Matcher matcher(String reportText, String regex) {
    Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
    return pattern.matcher(reportText);
  }

  protected String extractGroup(Matcher matcher, ParsingRegex regexEnum) {
    return matcher.group(regexEnum.groupName());
  }

}
