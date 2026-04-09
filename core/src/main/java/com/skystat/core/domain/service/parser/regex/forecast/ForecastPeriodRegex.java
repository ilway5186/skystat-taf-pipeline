package com.skystat.core.domain.service.parser.regex.forecast;

import com.skystat.core.domain.service.parser.regex.core.ParsingRegex;

public enum ForecastPeriodRegex implements ParsingRegex {

  FROM("from"),
  TO("to");

  private final String groupName;

  private ForecastPeriodRegex(String groupName) {
    this.groupName = groupName;
  }

  public static String regex() {
    return lookaheadCondition() +
      optionalFmPrefix() +
      fromGroup() +
      optionalToGroup();
  }

  // FM120300, 1203/1206 같은 형태인지 검사만 해봄
  private static String lookaheadCondition() {
    return "(?=FM\\d{6}\\b|\\d{4}/\\d{4}\\b)";
  }

  // FM으로 시작하면 확인. 단, 별도 그룹으로 추출하지는 않고 버림
  private static String optionalFmPrefix() {
    return "(?:FM)?";
  }

  // 1203, 120300형태 전부 추출
  private static String fromGroup() {
    return "(?<from>\\d{4}(?:\\d{2})?)";
  }

  // FM 같은 경우엔 to가 없음
  private static String optionalToGroup() {
    return "(?:/(?<to>\\d{4}))?";
  }

  @Override
  public String groupName() {
    return groupName;
  }

}
