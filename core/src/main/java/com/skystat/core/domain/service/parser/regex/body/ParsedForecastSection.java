package com.skystat.core.domain.service.parser.regex.body;

import com.skystat.core.domain.vo.taf.ChangeIndicator;
import com.skystat.core.domain.vo.taf.ForecastPeriod;
import com.skystat.core.domain.vo.weather.field.Visibility;
import com.skystat.core.domain.vo.weather.field.cloud.Cloud;
import com.skystat.core.domain.vo.weather.field.phenomena.Weather;
import com.skystat.core.domain.vo.weather.field.wind.Wind;

import java.util.List;
import java.util.Optional;

record ParsedForecastSection(
  String forecastRaw,
  ChangeIndicator indicator,
  ForecastPeriod period,
  Optional<Wind> wind,
  Optional<Visibility> visibility,
  List<Weather> weathers,
  List<Cloud> clouds,
  boolean isCavok,
  boolean isP6SM
) {
  ParsedForecastSection {
    if (wind.isEmpty()) wind = Optional.empty();
    if (visibility.isEmpty()) visibility = Optional.empty();
    if (weathers == null) weathers = List.of();
    if (clouds == null) clouds = List.of();
  }
}
