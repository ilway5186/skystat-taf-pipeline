package com.skystat.core.domain.service.parser.regex.body;

import com.skystat.core.domain.vo.taf.ForecastBody;
import com.skystat.core.domain.vo.weather.field.Visibility;
import com.skystat.core.domain.vo.weather.field.cloud.Cloud;
import com.skystat.core.domain.vo.weather.field.phenomena.Weather;
import com.skystat.core.domain.vo.weather.field.wind.Wind;
import com.skystat.core.exception.ErrorCode;
import com.skystat.core.exception.ParsingException;

import java.util.List;

public class ForecastBodyResolver {

  ForecastBody resolve(ParsedForecastSection parsedSection, ReferenceFields referenceFields, String rawSection) {
    return switch (parsedSection.indicator().referencePolicy()) {
      case REPLACE -> resolveReplaceable(parsedSection, rawSection);
      case MERGE, NONE -> resolveWithReference(parsedSection, referenceFields, rawSection);
    };
  }

  private ForecastBody resolveReplaceable(ParsedForecastSection parsedSection, String rawSection) {
    return createBody(
      parsedSection,
      parsedSection.wind().orElseThrow(() -> new ParsingException(ErrorCode.MISSING_WIND, rawSection)),
      parsedSection.visibility().orElseThrow(() -> new ParsingException(ErrorCode.MISSING_VISIBILITY, rawSection)),
      parsedSection.weathers(),
      parsedSection.clouds()
    );
  }

  private ForecastBody resolveWithReference(
    ParsedForecastSection parsedSection,
    ReferenceFields referenceFields,
    String rawSection
  ) {
    if (referenceFields == null) {
      throw new ParsingException(ErrorCode.INVALID_TAF_FORMAT, "Reference fields not established: " + rawSection);
    }

    Wind resolvedWind = parsedSection.wind().orElse(referenceFields.wind());
    Visibility resolvedVisibility = parsedSection.visibility().orElse(referenceFields.visibility());
    List<Weather> resolvedWeathers = !parsedSection.weathers().isEmpty() ? parsedSection.weathers() : referenceFields.weathers();
    List<Cloud> resolvedClouds = !parsedSection.clouds().isEmpty() ? parsedSection.clouds() : referenceFields.clouds();

    return createBody(parsedSection, resolvedWind, resolvedVisibility, resolvedWeathers, resolvedClouds);
  }

  private ForecastBody createBody(
    ParsedForecastSection parsedSection,
    Wind wind,
    Visibility visibility,
    List<Weather> weathers,
    List<Cloud> clouds
  ) {
    return new ForecastBody(
      parsedSection.forecastRaw(),
      parsedSection.indicator(),
      parsedSection.period(),
      wind,
      visibility,
      weathers,
      clouds,
      visibility != null && visibility.isCavok(),
      visibility != null && visibility.isP6SM()
    );
  }
}
