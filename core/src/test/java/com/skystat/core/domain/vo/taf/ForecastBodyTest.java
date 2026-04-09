package com.skystat.core.domain.vo.taf;

import com.skystat.core.domain.vo.weather.field.Visibility;
import com.skystat.core.domain.vo.weather.unit.LengthUnit;
import com.skystat.core.exception.InvalidInputException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.skystat.core.domain.support.DomainFixtures.period;
import static com.skystat.core.domain.support.DomainFixtures.wind;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ForecastBodyTest {

  @Test
  void CAVOK과_P6SM은_동시에_true일_수_없다() {
    assertThrows(InvalidInputException.class, () -> new ForecastBody(
      "HEADER",
      ChangeIndicator.HEADER,
      period("2026-04-09T09:00:00Z", "2026-04-10T06:00:00Z"),
      wind(90, 10, null),
      Visibility.cavok(),
      List.of(),
      List.of(),
      true,
      true
    ));
  }

  @Test
  void CAVOK은_최대시정이_필수다() {
    assertThrows(InvalidInputException.class, () -> new ForecastBody(
      "HEADER",
      ChangeIndicator.HEADER,
      period("2026-04-09T09:00:00Z", "2026-04-10T06:00:00Z"),
      wind(90, 10, null),
      new Visibility(9000, LengthUnit.METER, false, false),
      List.of(),
      List.of(),
      true,
      false
    ));
  }

  @Test
  void P6SM은_최대시정이_필수다() {
    assertThrows(InvalidInputException.class, () -> new ForecastBody(
      "HEADER",
      ChangeIndicator.HEADER,
      period("2026-04-09T09:00:00Z", "2026-04-10T06:00:00Z"),
      wind(90, 10, null),
      new Visibility(5.0, LengthUnit.SM, false, false),
      List.of(),
      List.of(),
      false,
      true
    ));
  }

  @Test
  void 정상적인_예보본문은_생성된다() {
    assertDoesNotThrow(() -> new ForecastBody(
      "HEADER",
      ChangeIndicator.HEADER,
      period("2026-04-09T09:00:00Z", "2026-04-10T06:00:00Z"),
      wind(90, 10, null),
      new Visibility(4000, LengthUnit.METER, false, false),
      List.of(),
      List.of(),
      false,
      false
    ));
  }
}
