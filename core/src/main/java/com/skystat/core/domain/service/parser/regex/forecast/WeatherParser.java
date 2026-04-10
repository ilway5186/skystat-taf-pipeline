package com.skystat.core.domain.service.parser.regex.forecast;

import com.skystat.core.domain.service.parser.FieldParser;
import com.skystat.core.domain.service.parser.regex.core.RegexParsingSupport;
import com.skystat.core.domain.vo.weather.field.phenomena.Weather;
import com.skystat.core.domain.vo.weather.field.phenomena.WeatherDescriptor;
import com.skystat.core.domain.vo.weather.field.phenomena.WeatherIntensity;
import com.skystat.core.domain.vo.weather.field.phenomena.WeatherPhenomenon;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;

public class WeatherParser extends RegexParsingSupport implements FieldParser<List<Weather>> {

  @Override
  public List<Weather> parse(String reportText) {
    Matcher matcher = matcher(reportText, WeatherRegex.regex());

    List<Weather> weathers = new ArrayList<>();
    while (matcher.find()) {
      String rawCode = matcher.group(0);

      String intensityGroup = matcher.group(WeatherRegex.INTENSITY.groupName());
      WeatherIntensity intensity = intensityGroup == null ? WeatherIntensity.MODERATE : WeatherIntensity.fromSymbol(intensityGroup);

      List<WeatherDescriptor> descriptors = parseToken(
        matcher.group(WeatherRegex.DESCRIPTOR.groupName()),
        WeatherRegex.descriptorGroup(),
        WeatherDescriptor::fromSymbol
      );

      List<WeatherPhenomenon> phenomena = parseToken(
        matcher.group(WeatherRegex.PHENOMENON.groupName()),
        WeatherRegex.phenomenonGroup(),
        WeatherPhenomenon::fromSymbol
      );

      if (descriptors.isEmpty() && phenomena.isEmpty()) continue;

      weathers.add(new Weather(rawCode, intensity, descriptors, phenomena));
    }

    return weathers;
  }

  private <T> List<T> parseToken(String matchedPart, String regex, Function<String, T> converter) {
    if (matchedPart == null || matchedPart.isBlank()) {
      return List.of();
    }

    List<T> result = new ArrayList<>();
    Matcher matcher = matcher(matchedPart, regex);
    while (matcher.find()) {
      result.add(converter.apply(matcher.group(0)));
    }

    return result;
  }

}
