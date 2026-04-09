package com.skystat.core.domain.service.parser.regex.body;

import com.skystat.core.domain.vo.taf.ForecastBody;
import com.skystat.core.domain.vo.weather.field.Visibility;
import com.skystat.core.domain.vo.weather.field.cloud.Cloud;
import com.skystat.core.domain.vo.weather.field.phenomena.Weather;
import com.skystat.core.domain.vo.weather.field.wind.Wind;

import java.util.List;

record ReferenceFields(
  Wind wind,
  Visibility visibility,
  List<Weather> weathers,
  List<Cloud> clouds
) {
  static ReferenceFields from(ForecastBody body) {
    return new ReferenceFields(
      body.wind(),
      body.visibility(),
      body.weathers(),
      body.clouds()
    );
  }
}
