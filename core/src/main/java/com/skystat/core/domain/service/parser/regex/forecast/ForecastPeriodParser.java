package com.skystat.core.domain.service.parser.regex.forecast;

import com.skystat.core.domain.service.parser.TemporalFieldParser;
import com.skystat.core.domain.vo.taf.ForecastPeriod;
import com.skystat.core.exception.ErrorCode;
import com.skystat.core.domain.service.parser.regex.core.RegexParsingSupport;
import com.skystat.core.exception.ParsingException;

import java.time.*;
import java.util.regex.Matcher;

public class ForecastPeriodParser extends RegexParsingSupport implements TemporalFieldParser<ForecastPeriod> {

  @Override
  public ForecastPeriod parse(String reportText, Instant referenceInstant) {
    Matcher matcher = matcher(reportText, ForecastPeriodRegex.regex());

    if (!matcher.find()) {
      throw new ParsingException(ErrorCode.MISSING_FORECAST_PERIOD, reportText);
    }

    String fromGroup = extractGroup(matcher, ForecastPeriodRegex.FROM);
    String toGroup = extractGroup(matcher, ForecastPeriodRegex.TO);

    Instant from = parseTime(fromGroup, referenceInstant);
    Instant to = (toGroup == null) ? from : parseTime(toGroup, referenceInstant);

    return new ForecastPeriod(from, to);
  }

  private Instant parseTime(String time, Instant referenceInstant) {
    int day = parseDay(time);
    int hour = parseHour(time);
    int minute = parseMinute(time);
    YearMonth parsedYearMonth = parseYearMonth(day, referenceInstant);

    return ZonedDateTime.of(
      LocalDateTime.of(parsedYearMonth.getYear(), parsedYearMonth.getMonthValue(), day, hour, minute),
      ZoneOffset.UTC
    ).toInstant();
  }

  private int parseDay(String time) {
    return Integer.parseInt(time.substring(0,2));
  }

  private int parseHour(String time) {
    return Integer.parseInt(time.substring(2,4));
  }

  private int parseMinute(String time) {
    return time.length() == 6 ? Integer.parseInt(time.substring(4,6)) : 0;
  }

  private YearMonth parseYearMonth(int parsedDay, Instant referenceInstant) {
    ZonedDateTime referenceDateTimeUtc = referenceInstant.atZone(ZoneOffset.UTC);
    YearMonth currentYearMonthUtc = YearMonth.from(referenceDateTimeUtc);
    int currentDayUtc = referenceDateTimeUtc.getDayOfMonth();

    if (parsedDay > currentDayUtc + 15) {
      return currentYearMonthUtc.minusMonths(1);
    } else if (parsedDay < currentDayUtc - 15) {
      return currentYearMonthUtc.plusMonths(1);
    }

    return currentYearMonthUtc;
  };

}
