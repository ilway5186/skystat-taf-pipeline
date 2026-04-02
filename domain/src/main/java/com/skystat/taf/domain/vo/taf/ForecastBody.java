package com.skystat.taf.domain.vo.taf;

import com.skystat.taf.domain.vo.weather.field.Visibility;
import com.skystat.taf.domain.vo.weather.field.cloud.Cloud;
import com.skystat.taf.domain.vo.weather.field.phenomena.Weather;
import com.skystat.taf.domain.vo.weather.unit.LengthUnit;
import com.skystat.taf.domain.vo.weather.field.wind.Wind;

import java.util.List;
import java.util.Objects;

public record ForecastBody(
  ChangeIndicator indicator,
  ForecastPeriod period,

  Wind wind,
  Visibility visibility,
  List<Weather> weathers,
  List<Cloud> clouds,

  boolean isCavok,
  boolean isP6SM
) {

  public ForecastBody {
    Objects.requireNonNull(indicator, "indicator cannot be null.");
    Objects.requireNonNull(period, "period cannot be null.");

    if (weathers == null) weathers = List.of();
    if (clouds == null) clouds = List.of();

    if (isCavok && isP6SM) {
      // CAVOK과 P6SM은 동시에 true일 수 없습니다.
      throw new IllegalArgumentException("CAVOK and P6SM cannot be true at the same time.");
    }

    if (isCavok) {
      if (visibility == null) {
        throw new IllegalArgumentException("CAVOK requires visibility.");
      }
      if (!isValidMaxVisibility(visibility)) {
        throw new IllegalArgumentException("CAVOK requires visibility to be 10000 meters.");
      }
    }

    if (isP6SM) {
      if (visibility == null) {
        throw new IllegalArgumentException("P6SM requires visibility.");
      }
      if (!isValidMaxVisibility(visibility)) {
        throw new IllegalArgumentException("P6SM requires visibility to be 10000 meters.");
      }
    }
  }

  private static boolean isValidMaxVisibility(Visibility visibility) {
    return visibility.unit() == LengthUnit.METER && visibility.value() == Visibility.MAX_VISIBILITY_METER;
  }

}