package com.skystat.core.domain.condition;

import com.skystat.core.domain.vo.taf.ChangeIndicator;
import com.skystat.core.domain.vo.taf.ForecastBody;
import com.skystat.core.domain.vo.weather.unit.SpeedUnit;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.skystat.core.domain.support.DomainFixtures.forecastBody;
import static com.skystat.core.domain.support.DomainFixtures.period;
import static com.skystat.core.domain.support.DomainFixtures.visibilityMeter;
import static com.skystat.core.domain.support.DomainFixtures.wind;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PeakWindConditionTest {

  @Test
  void 최대풍속조건을_판단한다() {
    ForecastBody forecastBody = forecastBody(
      ChangeIndicator.HEADER,
      period("2026-04-09T09:00:00Z", "2026-04-10T06:00:00Z"),
      wind(90, 10, 25.0),
      visibilityMeter(5000),
      List.of(),
      List.of()
    );

    assertTrue(new PeakWindCondition(20, SpeedUnit.KT, Comparison.GT).matches(forecastBody));
    assertFalse(new PeakWindCondition(30, SpeedUnit.KT, Comparison.GT).matches(forecastBody));
  }

  @Test
  void 풍속정보가_없으면_false다() {
    ForecastBody forecastBody = forecastBody(
      ChangeIndicator.HEADER,
      period("2026-04-09T09:00:00Z", "2026-04-10T06:00:00Z"),
      null,
      visibilityMeter(5000),
      List.of(),
      List.of()
    );

    assertFalse(new PeakWindCondition(20, SpeedUnit.KT, Comparison.GT).matches(forecastBody));
  }
}
