package com.skystat.core.domain.service.parser.regex.forecast;

import com.skystat.core.domain.vo.weather.field.phenomena.WeatherDescriptor;
import com.skystat.core.domain.vo.weather.field.phenomena.WeatherIntensity;
import com.skystat.core.domain.vo.weather.field.phenomena.WeatherPhenomenon;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public enum WeatherRegex {

  INTENSITY("intensity"),
  DESCRIPTOR("descriptor"),
  PHENOMENON("phenomenon");

  private final String groupName;

  public static String regex() {
    return String.format(
            "(?<!\\S)" +
            "(?<%1$s>%2$s)?" +
            "(?<%3$s>(%4$s){1,3})?" +
            "(?<%5$s>(?:%6$s){1,3})?" +
            "(?!\\S)",
            INTENSITY.groupName, intensityGroup(),
            DESCRIPTOR.groupName, descriptorGroup(),
            PHENOMENON.groupName, phenomenonGroup()
    );
  }

  public static String intensityGroup() {
    return Arrays.stream(WeatherIntensity.values())
        .map(WeatherIntensity::symbol)
        .filter(symbol -> !symbol.isEmpty())
        .map(Pattern::quote)
        .collect(Collectors.joining("|"));
  }

  public static String descriptorGroup() {
    return Arrays.stream(WeatherDescriptor.values())
        .map(WeatherDescriptor::symbol)
        .collect(Collectors.joining("|"));
  }

  public static String phenomenonGroup() {
    return Arrays.stream(WeatherPhenomenon.values())
        .map(WeatherPhenomenon::symbol)
        .collect(Collectors.joining("|"));
  }
}
