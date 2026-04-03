package com.skystat.taf.ingestion.service.parsing.field;

import com.skystat.taf.domain.vo.weather.field.wind.Wind;
import com.skystat.taf.domain.vo.weather.field.wind.WindDirection;
import com.skystat.taf.domain.vo.weather.field.wind.WindDirectionType;
import com.skystat.taf.domain.vo.weather.unit.SpeedUnit;
import com.skystat.taf.ingestion.exception.InvalidInputException;
import com.skystat.taf.ingestion.service.parsing.ReportRegexParser;

import java.util.regex.Matcher;

public class WindParser extends ReportRegexParser<Wind> {

  @Override
  public Wind parse(String rawText) {
    Matcher matcher = matcher(rawText, WindRegex.regex());

    if (!matcher.find()) {
      // 전문 내 바람정보를 찾을 수 없습니다
      throw new InvalidInputException("Wind not found in report:  " + rawText);
    }

    return new Wind(
      parseDirection(matcher),
      parseSpeed(matcher, false),
      parseSpeed(matcher, true),
      parseUnit(matcher)
    );
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
