package com.skystat.core.domain.service.parser.regex.header;

import com.skystat.core.domain.service.parser.regex.core.ParsingRegex;

public enum IssuedTimeRegex implements ParsingRegex {

  DAY("day"),
  HOUR("hour"),
  MINUTE("minute");

  private final String groupName;

  IssuedTimeRegex(String groupName) {
    this.groupName = groupName;
  }

  public static String regex() {
    return String.format("(%s)(%s)(%s)Z",
            dayGroup(),
            hourGroup(),
            minuteGroup());
  }

  private static String dayGroup() {
    return "(?<day>\\d{2})";
  }

  private static String hourGroup() {
    return "(?<hour>\\d{2})";
  }

  private static String minuteGroup() {
    return "(?<minute>\\d{2})";
  }

  @Override
  public String groupName() {
    return groupName;
  }

}
