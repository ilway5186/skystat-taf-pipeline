package com.skystat.core.domain.service.parser.regex.forecast;

import com.skystat.core.domain.service.parser.regex.core.ParsingRegex;
import com.skystat.core.domain.vo.weather.unit.SpeedUnit;

import java.util.Arrays;
import java.util.stream.Collectors;

public enum WindRegex implements ParsingRegex {

  DIRECTION("direction"),
  SPEED("speed"),
  GUST("gust"),
  UNIT("unit");

  private final String groupName;

  WindRegex(String groupName) {
    this.groupName = groupName;
  }

  @Override
  public String groupName() {
    return groupName;
  }

  public static String regex() {
    return String.format("(?:^|\\s)(%s)(%s)(%s)?(%s)(?=(?:\\s|$))",
      directionGroup(),
      speedGroup(),
      gustGroup(),
      unitGroup()
    );
  }

  private static String directionGroup() {
    return "(?<direction>\\d{3}|VRB)";
  }

  private static String speedGroup() {
    return "(?<speed>P?\\d{2,3})";
  }

  private static String gustGroup() {
    return "G(?<gust>P?\\d{2,3})";
  }

  private static String unitGroup() {
    return Arrays.stream(SpeedUnit.values())
      .map(Enum::name)
      .collect(Collectors.joining("|", "(?<unit>", ")"));
  }

}
