package com.skystat.taf.domain.entity;

import com.skystat.taf.domain.condition.ForecastCondition;
import com.skystat.taf.domain.vo.taf.*;
import com.skystat.taf.domain.vo.weather.field.IssuedTime;
import com.skystat.taf.domain.vo.weather.field.temperature.TemperatureExtremeType;

import java.util.List;
import java.util.Objects;

public class Taf {

  private TafId tafId;

  private String reportText;

  private ReportType reportType;
  private String station;
  private IssuedTime issuedTime;
  private ForecastPeriod validPeriod;

  private List<ForecastBody> body;
  private List<ForecastTemperature> temperatures;

  public List<ForecastPeriod> findPeriodsMatching(ForecastCondition condition) {
    Objects.requireNonNull(condition, "condition cannot be null.");

    return body.stream()
      .filter(condition::matches)
      .map(ForecastBody::period)
      .toList();
  }

}
