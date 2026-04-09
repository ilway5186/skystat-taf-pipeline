package com.skystat.core.domain.support;

import com.skystat.core.domain.vo.taf.ChangeIndicator;
import com.skystat.core.domain.vo.taf.ForecastBody;
import com.skystat.core.domain.vo.taf.ForecastPeriod;
import com.skystat.core.domain.vo.weather.field.Visibility;
import com.skystat.core.domain.vo.weather.field.cloud.Cloud;
import com.skystat.core.domain.vo.weather.field.cloud.CloudCoverage;
import com.skystat.core.domain.vo.weather.field.cloud.CloudType;
import com.skystat.core.domain.vo.weather.field.phenomena.Weather;
import com.skystat.core.domain.vo.weather.field.phenomena.WeatherIntensity;
import com.skystat.core.domain.vo.weather.field.phenomena.WeatherPhenomenon;
import com.skystat.core.domain.vo.weather.field.wind.Wind;
import com.skystat.core.domain.vo.weather.field.wind.WindDirection;
import com.skystat.core.domain.vo.weather.unit.LengthUnit;
import com.skystat.core.domain.vo.weather.unit.SpeedUnit;

import java.time.Instant;
import java.util.List;

public final class DomainFixtures {

  private DomainFixtures() {
  }

  public static ForecastPeriod period(String from, String to) {
    return new ForecastPeriod(Instant.parse(from), Instant.parse(to));
  }

  public static Wind wind(double degree, double speedKt, Double gustKt) {
    return new Wind(WindDirection.fixed(degree), speedKt, gustKt, SpeedUnit.KT);
  }

  public static Visibility visibilityMeter(double value) {
    return new Visibility(value, LengthUnit.METER, false, false);
  }

  public static Weather rain() {
    return new Weather("RA", WeatherIntensity.MODERATE, List.of(), List.of(WeatherPhenomenon.RAIN));
  }

  public static Cloud brokenCloud(double altitudeFt) {
    return new Cloud("BKN030", CloudCoverage.BROKEN, altitudeFt, CloudType.NONE);
  }

  public static ForecastBody forecastBody(
    ChangeIndicator indicator,
    ForecastPeriod period,
    Wind wind,
    Visibility visibility,
    List<Weather> weathers,
    List<Cloud> clouds
  ) {
    return new ForecastBody(
      indicator.name(),
      indicator,
      period,
      wind,
      visibility,
      weathers,
      clouds,
      visibility != null && visibility.isCavok(),
      visibility != null && visibility.isP6SM()
    );
  }
}
