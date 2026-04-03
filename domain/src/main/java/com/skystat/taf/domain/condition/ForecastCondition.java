package com.skystat.taf.domain.condition;

import com.skystat.taf.domain.vo.taf.ForecastBody;

public interface ForecastCondition {

  boolean matches(ForecastBody forecast);
}
