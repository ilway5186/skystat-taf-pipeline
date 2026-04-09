package com.skystat.core.domain.condition;

import com.skystat.core.domain.vo.taf.ChangeIndicator;
import com.skystat.core.domain.vo.taf.ForecastBody;
import com.skystat.core.domain.vo.weather.unit.LengthUnit;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.skystat.core.domain.support.DomainFixtures.forecastBody;
import static com.skystat.core.domain.support.DomainFixtures.period;
import static com.skystat.core.domain.support.DomainFixtures.visibilityMeter;
import static com.skystat.core.domain.support.DomainFixtures.wind;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VisibilityConditionTest {

  @Test
  void 시정조건을_판단한다() {
    ForecastBody forecastBody = forecastBody(
      ChangeIndicator.HEADER,
      period("2026-04-09T09:00:00Z", "2026-04-10T06:00:00Z"),
      wind(90, 10, null),
      visibilityMeter(3000),
      List.of(),
      List.of()
    );

    assertTrue(new VisibilityCondition(4000, LengthUnit.METER, Comparison.LT).matches(forecastBody));
    assertFalse(new VisibilityCondition(2000, LengthUnit.METER, Comparison.LT).matches(forecastBody));
  }

  @Test
  void 시정이_없으면_false다() {
    ForecastBody forecastBody = forecastBody(
      ChangeIndicator.HEADER,
      period("2026-04-09T09:00:00Z", "2026-04-10T06:00:00Z"),
      wind(90, 10, null),
      null,
      List.of(),
      List.of()
    );

    assertFalse(new VisibilityCondition(4000, LengthUnit.METER, Comparison.LT).matches(forecastBody));
  }
}
