package com.skystat.taf.domain.service.parser.field;

import com.skystat.taf.domain.exception.InvalidInputException;
import com.skystat.taf.domain.service.parser.ReportRegexParser;
import com.skystat.taf.domain.vo.taf.ForecastPeriod;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.regex.Matcher;

public class ForecastPeriodParser extends ReportRegexParser<ForecastPeriod> {

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
  public ForecastPeriod parse(String rawText) {
    Matcher matcher = matcher(rawText, ForecastPeriodRegex.regex());

    if (!matcher.find()) {
      throw new InvalidInputException("Forecast period not found in report:  " + rawText);
    }

    String fromGroup = extractGroup(matcher, ForecastPeriodRegex.FROM);
    String toGroup = extractGroup(matcher, ForecastPeriodRegex.TO);

    ZonedDateTime from = parseZonedDateTime(fromGroup);
    ZonedDateTime to = (toGroup == null) ? from : parseZonedDateTime(toGroup);

    return new ForecastPeriod(from, to);
  }

  private ZonedDateTime parseZonedDateTime(String time) {
    int day = parseDay(time);
    int hour = parseHour(time);
    int minute = parseMinute(time);
    YearMonth parsedYearMonth = parseYearMonth(day);

    return ZonedDateTime.of(
      LocalDateTime.of(parsedYearMonth.getYear(), parsedYearMonth.getMonthValue(), day, hour, minute),
      ZoneOffset.UTC
    );
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
