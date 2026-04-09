package com.skystat.core.domain.condition;

import com.skystat.core.domain.vo.taf.ForecastBody;

public interface ForecastCondition {

  boolean matches(ForecastBody forecast);
}
