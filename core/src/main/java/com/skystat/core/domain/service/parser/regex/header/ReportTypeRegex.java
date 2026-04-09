package com.skystat.core.domain.service.parser.regex.header;

import com.skystat.core.domain.service.parser.regex.core.ParsingRegex;
import com.skystat.core.domain.vo.taf.ReportType;

import java.util.Arrays;
import java.util.stream.Collectors;

public enum ReportTypeRegex implements ParsingRegex {

  TYPE("type");

  private final String groupName;

  ReportTypeRegex(String groupName) {
    this.groupName = groupName;
  }

  @Override
  public String groupName() {
    return groupName;
  }

  public static String regex() {
    return String.format("(%s)", typeGroup());
  }

  private static String typeGroup() {
    return Arrays.stream(ReportType.values())
      .map(Enum::name)
      .collect(Collectors.joining("|", "(?<type>", ")"));
  }

}
