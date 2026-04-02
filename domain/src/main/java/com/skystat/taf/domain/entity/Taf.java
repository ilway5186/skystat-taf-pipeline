package com.skystat.taf.domain.entity;

import com.skystat.taf.domain.vo.taf.*;
import com.skystat.taf.domain.vo.weather.field.IssuedTime;

import java.time.ZonedDateTime;
import java.util.List;

public class Taf {

  private TafId tafId;

  private String reportText;

  private ReportType reportType;
  private String station;
  private IssuedTime issuedTime;
  private ForecastPeriod validPeriod;

  private List<ForecastBody> body;
  private List<ForecastTemperature> forecastTemperature;



}
