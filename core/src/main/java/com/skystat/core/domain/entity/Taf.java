package com.skystat.core.domain.entity;

import com.skystat.core.domain.condition.ForecastCondition;
import com.skystat.core.domain.spec.taf.TafInvariantSpecification;
import com.skystat.core.exception.ErrorCode;
import com.skystat.core.exception.InvalidInputException;
import com.skystat.core.domain.vo.taf.*;
import com.skystat.core.domain.vo.weather.field.IssuedTime;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Objects;

@Getter
@Accessors(fluent = true)
public class Taf {

  private static final TafInvariantSpecification INVARIANT_SPECIFICATION = new TafInvariantSpecification();

  private final TafId tafId;

  private final String reportText;

  private final ReportType reportType;
  private final String stationIcao;
  private final IssuedTime issuedTime;
  private final ForecastPeriod validPeriod;

  private final List<ForecastBody> body;
  private final List<ForecastTemperature> temperatures;

  @Builder
  private Taf(
    TafId tafId,
    String reportText,
    ReportType reportType,
    String stationIcao,
    IssuedTime issuedTime,
    ForecastPeriod validPeriod,
    List<ForecastBody> body,
    List<ForecastTemperature> temperatures
  ) {
    this.tafId = requireNonNull(tafId, "tafId");
    this.reportText = requireNonBlank(reportText, "reportText");
    this.reportType = requireNonNull(reportType, "reportType");
    this.stationIcao = requireNonBlank(stationIcao, "stationIcao");
    this.issuedTime = requireNonNull(issuedTime, "issuedTime");
    this.validPeriod = requireNonNull(validPeriod, "validPeriod");
    this.body = List.copyOf(requireNonNull(body, "body"));
    this.temperatures = List.copyOf(requireNonNull(temperatures, "temperatures"));

    INVARIANT_SPECIFICATION.check(this);
  }

  public List<ForecastPeriod> findPeriodsMatching(ForecastCondition condition) {
    Objects.requireNonNull(condition, "condition cannot be null.");

    return body.stream()
      .filter(condition::matches)
      .map(ForecastBody::period)
      .toList();
  }

  public ForecastBody header() {
    return body.stream()
      .filter(forecastBody -> forecastBody.indicator() == ChangeIndicator.HEADER)
      .findFirst()
      .orElseThrow(() -> new InvalidInputException(ErrorCode.INVALID_INPUT, "body must contain exactly one HEADER forecast."));
  }

  private static <T> T requireNonNull(T value, String fieldName) {
    if (value == null) {
      throw new InvalidInputException(ErrorCode.INVALID_INPUT, fieldName + " cannot be null.");
    }
    return value;
  }

  private static String requireNonBlank(String value, String fieldName) {
    if (value == null || value.isBlank()) {
      throw new InvalidInputException(ErrorCode.INVALID_INPUT, fieldName + " cannot be blank.");
    }
    return value;
  }

}
