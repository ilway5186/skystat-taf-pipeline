package com.skystat.core.domain.service.parser.regex.forecast;

import com.skystat.core.domain.service.parser.FieldParser;
import com.skystat.core.domain.vo.weather.field.wind.Wind;
import com.skystat.core.domain.vo.weather.field.wind.WindDirection;
import com.skystat.core.domain.vo.weather.field.wind.WindDirectionType;
import com.skystat.core.domain.vo.weather.unit.SpeedUnit;
import com.skystat.core.domain.service.parser.regex.core.RegexParsingSupport;

import java.util.Optional;
import java.util.regex.Matcher;

public class WindParser extends RegexParsingSupport implements FieldParser<Optional<Wind>> {

  @Override
  public Optional<Wind> parse(String reportText) {
    Matcher matcher = matcher(reportText, WindRegex.regex());

    if (!matcher.find()) {
      return Optional.empty();
    }

    return Optional.of(new Wind(
      parseDirection(matcher),
      parseSpeed(matcher, false),
      parseSpeed(matcher, true),
      parseUnit(matcher)
    ));
  }

  private SpeedUnit parseUnit(Matcher matcher) {
    String unitGroup = extractGroup(matcher, WindRegex.UNIT);
    return SpeedUnit.valueOf(unitGroup);
  }

  private WindDirection parseDirection(Matcher matcher) {
    String directionGroup = extractGroup(matcher, WindRegex.DIRECTION);

    if (directionGroup.equals(WindDirectionType.VARIABLE.symbol())) {
      return WindDirection.variable();
    }
    return WindDirection.fixed(Double.parseDouble(directionGroup));
  }

  private Double parseSpeed(Matcher matcher, boolean isGust) {
    String speedGroup = isGust ? extractGroup(matcher, WindRegex.GUST) : extractGroup(matcher, WindRegex.SPEED);

    if (speedGroup == null || speedGroup.isBlank()) return null;
    if (speedGroup.startsWith("P")) {
      return Double.parseDouble(speedGroup.substring(1));
    }
    return Double.parseDouble(speedGroup);
  }


}
