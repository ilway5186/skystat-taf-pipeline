package com.skystat.taf.domain.service.parser.field;

import com.skystat.taf.domain.service.parser.ParsingRegex;
import com.skystat.taf.domain.vo.taf.ChangeIndicator;
import com.skystat.taf.domain.vo.taf.ReportType;

import java.util.Arrays;
import java.util.stream.Collectors;

public enum ChangeIndicatorRegex implements ParsingRegex {

  INDICATOR("indicator"),;

  private final String groupName;

  ChangeIndicatorRegex(String groupName) {
    this.groupName = groupName;
  }

  public static String regex() {
    return String.format("(%s)", indicatorRegex());
  }

  private static String indicatorRegex() {
    return Arrays.stream(ChangeIndicator.values())
      .map(Enum::name)
      .map(s -> s.replace("_", " "))
      .collect(Collectors.joining("|", "(?<indicator>)", ")"));
  }

  @Override
  public String groupName() {
    return groupName;
  }

}
