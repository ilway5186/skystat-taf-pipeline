package com.skystat.core.domain.condition;

import com.skystat.core.domain.vo.taf.ChangeIndicator;
import com.skystat.core.domain.vo.taf.ForecastBody;
import com.skystat.core.domain.vo.weather.field.cloud.CloudCoverage;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.skystat.core.domain.support.DomainFixtures.brokenCloud;
import static com.skystat.core.domain.support.DomainFixtures.forecastBody;
import static com.skystat.core.domain.support.DomainFixtures.period;
import static com.skystat.core.domain.support.DomainFixtures.visibilityMeter;
import static com.skystat.core.domain.support.DomainFixtures.wind;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CloudAltitudeConditionTest {

  @Test
  void 구름고도조건을_판단한다() {
    ForecastBody forecastBody = forecastBody(
      ChangeIndicator.HEADER,
      period("2026-04-09T09:00:00Z", "2026-04-10T06:00:00Z"),
      wind(90, 10, null),
      visibilityMeter(5000),
      List.of(),
      List.of(brokenCloud(3000))
    );

    assertTrue(new CloudAltitudeCondition(CloudCoverage.BROKEN, 5000, Comparison.LT).matches(forecastBody));
    assertFalse(new CloudAltitudeCondition(CloudCoverage.BROKEN, 2000, Comparison.LT).matches(forecastBody));
  }
}
