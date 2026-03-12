package com.skystat.taf.domain.entity;

import com.skystat.taf.domain.vo.taf.ForecastBody;
import com.skystat.taf.domain.vo.taf.ForecastPeriod;
import com.skystat.taf.domain.vo.taf.ForecastTemperature;
import com.skystat.taf.domain.vo.taf.ReportType;

import java.time.ZonedDateTime;
import java.util.List;

public class Taf {

  private String rawText;

  private ReportType reportType;
  private String station;
  private ZonedDateTime issuedTime;
  private ForecastPeriod forecastPeriod;

  private List<ForecastBody> body;
  private List<ForecastTemperature> forecastTemperature;

}
