package com.skystat.core.domain.service.parser.regex.forecast;

import com.skystat.core.domain.vo.weather.field.cloud.CloudCoverage;
import com.skystat.core.domain.vo.weather.field.cloud.CloudType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Arrays;
import java.util.stream.Collectors;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public enum CloudRegex {

  COVERAGE("coverage"),
  TYPE("type"),
  ALTITUDE("altitude");

  private final String groupName;

  public static String regex() {
    return String.format("(%s)(%s)?(%s)?",
            coverageGroup(),
            altitudeGroup(),
            typeGroup());
  }

  private static String coverageGroup() {
    return Arrays.stream(CloudCoverage.values())
            .map(CloudCoverage::symbol)
            .collect(Collectors.joining("|", "(?<coverage>", ")"));
  }

  private static String typeGroup() {
    return Arrays.stream(CloudType.values())
            .map(CloudType::symbol)
            .collect(Collectors.joining("|", "(?<type>", ")"));
  }

  private static String altitudeGroup() {
    return "(?<altitude>\\d{2,3}|/{2,3})";
  }

}
