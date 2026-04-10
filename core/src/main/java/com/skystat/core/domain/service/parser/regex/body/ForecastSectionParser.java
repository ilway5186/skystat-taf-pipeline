package com.skystat.core.domain.service.parser.regex.body;

import com.skystat.core.domain.service.parser.regex.forecast.CloudParser;
import com.skystat.core.domain.service.parser.regex.forecast.ForecastPeriodParser;
import com.skystat.core.domain.service.parser.regex.forecast.VisibilityParser;
import com.skystat.core.domain.service.parser.regex.forecast.WeatherParser;
import com.skystat.core.domain.service.parser.regex.forecast.WindParser;
import com.skystat.core.domain.vo.weather.field.Visibility;
import com.skystat.core.domain.vo.weather.field.cloud.Cloud;
import com.skystat.core.domain.vo.weather.field.phenomena.Weather;
import com.skystat.core.domain.vo.weather.field.wind.Wind;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class ForecastSectionParser {

  private final ForecastPeriodParser forecastPeriodParser;
  private final WindParser windParser;
  private final VisibilityParser visibilityParser;
  private final WeatherParser weatherParser;
  private final CloudParser cloudParser;

  ParsedForecastSection parse(ForecastSection section, Instant referenceInstant) {
    Optional<Visibility> visibility = visibilityParser.parse(section.sectionRaw());
    Optional<Wind> wind = windParser.parse(section.sectionRaw());
    List<Weather> weathers = weatherParser.parse(section.sectionRaw());
    List<Cloud> clouds = cloudParser.parse(section.sectionRaw());

    return new ParsedForecastSection(
      section.sectionRaw(),
      section.indicator(),
      forecastPeriodParser.parse(section.sectionRaw(), referenceInstant),
      wind,
      visibility,
      weathers,
      clouds,
      visibility.map(Visibility::isCavok).orElse(false),
      visibility.map(Visibility::isP6SM).orElse(false)
    );
  }
}
