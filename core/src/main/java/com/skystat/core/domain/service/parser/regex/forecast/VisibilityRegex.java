package com.skystat.core.domain.service.parser.regex.forecast;

import com.skystat.core.domain.service.parser.regex.core.ParsingRegex;

public enum VisibilityRegex implements ParsingRegex {

  DIGIT("digit"),
  CAVOK("cavok"),
  P6SM("p6sm"),
  MILE("mile"),
  FRACTION_MILE("fractionMile"),
  IMPROPER_FRACTION_MILE("improperFractionMile");

  private final String groupName;

  VisibilityRegex(String groupName) {
    this.groupName = groupName;
  }

  public static String regex() {
    return String.format("(?:^|\\s)(%s)(?=\\s|$)",
      String.join("|",
        improperFractionMileGroup(), // 우선순위 1: 띄어쓰기가 있는 대분수를 가장 먼저 검사
        fractionMileGroup(),         // 우선순위 2: 일반 분수
        mileGroup(),                 // 우선순위 3: 일반 마일
        digitGroup(),
        cavokGroup(),
        p6smGroup()
      )
    );
  }

  private static String digitGroup() {
    return "(?<digit>\\d{2,4})";
  }

  private static String cavokGroup() {
    return "(?<cavok>CAVOK)";
  }

  private static String p6smGroup() {
    return "(?<p6sm>P6SM)";
  }

  private static String mileGroup() {
    return "(?<mile>\\d+SM)";
  }

  private static String fractionMileGroup() {
    return "(?<fractionMile>\\d+/\\d+SM)";
  }

  private static String improperFractionMileGroup() {
    return "(?<improperFractionMile>\\d+\\s\\d+/\\d+SM)";
  }

  @Override
  public String groupName() {
    return groupName;
  }

}
