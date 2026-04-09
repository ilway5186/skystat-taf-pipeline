package com.skystat.core.domain.entity;

import com.skystat.core.domain.vo.taf.ChangeIndicator;
import com.skystat.core.domain.vo.taf.ForecastBody;
import com.skystat.core.domain.vo.taf.ForecastPeriod;
import com.skystat.core.domain.vo.taf.ForecastTemperature;
import com.skystat.core.domain.vo.taf.ReportType;
import com.skystat.core.domain.vo.taf.TafId;
import com.skystat.core.domain.vo.weather.field.IssuedTime;
import com.skystat.core.domain.vo.weather.field.temperature.Temperature;
import com.skystat.core.domain.vo.weather.field.temperature.TemperatureExtremeType;
import com.skystat.core.domain.vo.weather.unit.TemperatureUnit;
import com.skystat.core.exception.InvalidInputException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

class TafTest {

  @Test
  void TAF_필수_필드는_null_또는_공백일_수_없다() {
    assertThrows(InvalidInputException.class, () -> validBuilder().tafId(null).build());
    assertThrows(InvalidInputException.class, () -> validBuilder().reportText(" ").build());
    assertThrows(InvalidInputException.class, () -> validBuilder().reportType(null).build());
    assertThrows(InvalidInputException.class, () -> validBuilder().stationIcao(" ").build());
    assertThrows(InvalidInputException.class, () -> validBuilder().issuedTime(null).build());
    assertThrows(InvalidInputException.class, () -> validBuilder().validPeriod(null).build());
  }

  @Test
  void TAF_body는_비어있을_수_없다() {
    assertThrows(InvalidInputException.class, () -> validBuilder().body(List.of()).build());
  }

  @Test
  void TAF_body는_정확히_하나의_HEADER를_가져야_한다() {
    ForecastPeriod validPeriod = validPeriod();

    ForecastBody tempo = new ForecastBody(
      "TEMPO 0909/0911 3000",
      ChangeIndicator.TEMPO,
      validPeriod,
      null,
      null,
      List.of(),
      List.of(),
      false,
      false
    );

    ForecastBody anotherHeader = new ForecastBody(
      "TAF RKSI 090800Z 0909/1006 AMD",
      ChangeIndicator.HEADER,
      validPeriod,
      null,
      null,
      List.of(),
      List.of(),
      false,
      false
    );

    assertThrows(InvalidInputException.class, () -> validBuilder().body(List.of(tempo)).build());
    assertThrows(InvalidInputException.class, () -> validBuilder().body(List.of(headerBody(), anotherHeader)).build());
  }

  @Test
  void TAF_validPeriod는_HEADER_period와_일치해야_한다() {
    ForecastPeriod differentPeriod = new ForecastPeriod(
      Instant.parse("2026-04-09T10:00:00Z"),
      Instant.parse("2026-04-10T07:00:00Z")
    );

    assertThrows(InvalidInputException.class, () -> validBuilder().validPeriod(differentPeriod).build());
  }

  @Test
  void TAF_body_period는_validPeriod_범위_내여야_한다() {
    ForecastBody invalidTempo = new ForecastBody(
      "TEMPO 1005/1008 3000",
      ChangeIndicator.TEMPO,
      new ForecastPeriod(
        Instant.parse("2026-04-10T05:00:00Z"),
        Instant.parse("2026-04-10T08:00:00Z")
      ),
      null,
      null,
      List.of(),
      List.of(),
      false,
      false
    );

    assertThrows(InvalidInputException.class, () -> validBuilder().body(List.of(headerBody(), invalidTempo)).build());
  }

  @Test
  void TAF_temperature_time은_validPeriod_범위_내여야_한다() {
    ForecastTemperature invalidTemperature = new ForecastTemperature(
      TemperatureExtremeType.MAXIMUM,
      new Temperature(12.0, TemperatureUnit.CELSIUS),
      Instant.parse("2026-04-10T07:00:00Z")
    );

    assertThrows(InvalidInputException.class, () -> validBuilder().temperatures(List.of(invalidTemperature)).build());
  }

  private Taf.TafBuilder validBuilder() {
    Instant issuedAt = Instant.parse("2026-04-09T08:00:00Z");
    ForecastPeriod validPeriod = validPeriod();

    return Taf.builder()
      .tafId(TafId.of("23f3fb19b9d0d3c94ef22cd64d32790977a301c0d8bedfe62c81ceebec77da5a"))
      .reportText("TAF RKSI 090800Z 0909/1006 09010KT 9999=")
      .reportType(ReportType.ROUTINE)
      .stationIcao("RKSI")
      .issuedTime(new IssuedTime(issuedAt))
      .validPeriod(validPeriod)
      .body(List.of(headerBody()))
      .temperatures(List.of());
  }

  private ForecastPeriod validPeriod() {
    return new ForecastPeriod(
      Instant.parse("2026-04-09T09:00:00Z"),
      Instant.parse("2026-04-10T06:00:00Z")
    );
  }

  private ForecastBody headerBody() {
    return new ForecastBody(
      "TAF RKSI 090800Z 0909/1006",
      ChangeIndicator.HEADER,
      validPeriod(),
      null,
      null,
      List.of(),
      List.of(),
      false,
      false
    );
  }
}
