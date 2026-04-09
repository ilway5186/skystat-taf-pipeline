package com.skystat.core.domain.service.parser.regex.header;

import com.skystat.core.domain.service.parser.regex.core.ParsingRegex;

public enum StationIcaoRegex implements ParsingRegex {

  STATION("station");

  private final String groupName;

  StationIcaoRegex(String groupName) {
    this.groupName = groupName;
  }

  public static String regex() {
    return String.format("(?:^|\\s|[\\p{Punct}])(%s)\\s(\\d{6}Z)", stationRegex());
  }

  @Override
  public String groupName() {
    return groupName;
  }

  private static String stationRegex() {
    return "(?<station>[A-Z]{4})";
  }

}
