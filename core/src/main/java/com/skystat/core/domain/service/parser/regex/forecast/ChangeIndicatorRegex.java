package com.skystat.core.domain.service.parser.regex.forecast;

import com.skystat.core.domain.vo.taf.ChangeIndicator;
import com.skystat.core.domain.service.parser.regex.core.ParsingRegex;

public enum ChangeIndicatorRegex implements ParsingRegex {

  INDICATOR("indicator");

  private final String groupName;

  ChangeIndicatorRegex(String groupName) {
    this.groupName = groupName;
  }

  public static String regex() {
    return "(?<indicator>PROB30 TEMPO|PROB40 TEMPO|BECMG|TEMPO|INTER|PROB30|PROB40|FM\\d{6})";
  }

  @Override
  public String groupName() {
    return groupName;
  }

}
