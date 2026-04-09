package com.skystat.core.domain.condition;

import com.skystat.core.domain.vo.taf.ChangeIndicator;
import com.skystat.core.domain.vo.taf.ForecastBody;
import com.skystat.core.domain.vo.weather.field.phenomena.Weather;
import com.skystat.core.domain.vo.weather.field.phenomena.WeatherDescriptor;
import com.skystat.core.domain.vo.weather.field.phenomena.WeatherIntensity;
import com.skystat.core.domain.vo.weather.field.phenomena.WeatherPhenomenon;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.skystat.core.domain.support.DomainFixtures.forecastBody;
import static com.skystat.core.domain.support.DomainFixtures.period;
import static com.skystat.core.domain.support.DomainFixtures.visibilityMeter;
import static com.skystat.core.domain.support.DomainFixtures.wind;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WeatherCodeConditionTest {

  @Test
  void 날씨코드조건을_판단한다() {
    ForecastBody forecastBody = forecastBody(
      ChangeIndicator.HEADER,
      period("2026-04-09T09:00:00Z", "2026-04-10T06:00:00Z"),
      wind(90, 10, null),
      visibilityMeter(5000),
      List.of(new Weather(
        "TSRA",
        WeatherIntensity.MODERATE,
        List.of(WeatherDescriptor.THUNDERSTORM),
        List.of(WeatherPhenomenon.RAIN)
      )),
      List.of()
    );

    assertTrue(new WeatherCodeCondition(WeatherDescriptor.THUNDERSTORM).matches(forecastBody));
    assertTrue(new WeatherCodeCondition(WeatherPhenomenon.RAIN).matches(forecastBody));
    assertFalse(new WeatherCodeCondition(WeatherPhenomenon.FOG).matches(forecastBody));
  }
}
