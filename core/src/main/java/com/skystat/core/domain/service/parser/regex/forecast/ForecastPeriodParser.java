package com.skystat.core.domain.service.parser.regex.forecast;

import com.skystat.core.domain.vo.taf.ForecastPeriod;
import com.skystat.core.exception.ErrorCode;
import com.skystat.core.domain.service.parser.regex.core.RegexFieldParser;import com.skystat.core.exception.ParsingException;

import java.time.*;
import java.util.regex.Matcher;

public class ForecastPeriodParser extends RegexFieldParser<ForecastPeriod> {

  private final YearMonth yearMonth;

  public ForecastPeriodParser() {
    this.yearMonth = null;
  }

  private ForecastPeriodParser(YearMonth yearMonth) {
    this.yearMonth = yearMonth;
  }

  public static ForecastPeriodParser withYearMonth(YearMonth yearMonth) {
    return new ForecastPeriodParser(yearMonth);
  }

  @Override
  public ForecastPeriod parse(String reportText) {
    Matcher matcher = matcher(reportText, ForecastPeriodRegex.regex());

    if (!matcher.find()) {
      throw new ParsingException(ErrorCode.MISSING_FORECAST_PERIOD, reportText);
    }

    String fromGroup = extractGroup(matcher, ForecastPeriodRegex.FROM);
    String toGroup = extractGroup(matcher, ForecastPeriodRegex.TO);

    Instant from = parseTime(fromGroup);
    Instant to = (toGroup == null) ? from : parseTime(toGroup);

    return new ForecastPeriod(from, to);
  }

  private Instant parseTime(String time) {
    int day = parseDay(time);
    int hour = parseHour(time);
    int minute = parseMinute(time);
    YearMonth parsedYearMonth = parseYearMonth(day);

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

  private YearMonth parseYearMonth(int parsedDay) {
    if (this.yearMonth != null) return this.yearMonth;

    YearMonth currentYearMonthUtc = YearMonth.now(ZoneOffset.UTC);
    int currentDayUtc = ZonedDateTime.now(ZoneOffset.UTC).getDayOfMonth();

    if (parsedDay > currentDayUtc + 15) {
      return currentYearMonthUtc.minusMonths(1);
    } else if (parsedDay < currentDayUtc - 15) {
      return currentYearMonthUtc.plusMonths(1);
    }

    return currentYearMonthUtc;
  };

}
