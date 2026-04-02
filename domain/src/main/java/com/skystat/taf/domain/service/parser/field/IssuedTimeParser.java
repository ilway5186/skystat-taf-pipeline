package com.skystat.taf.domain.service.parser.field;

import com.skystat.taf.domain.exception.InvalidInputException;
import com.skystat.taf.domain.service.parser.ReportRegexParser;
import com.skystat.taf.domain.vo.weather.field.IssuedTime;

import java.time.*;
import java.util.regex.Matcher;

public class IssuedTimeParser extends ReportRegexParser<IssuedTime> {

  private final YearMonth yearMonth;

  public IssuedTimeParser() {
    this.yearMonth = null;
  }

  private IssuedTimeParser(YearMonth yearMonth) {
    this.yearMonth = yearMonth;
  }

  public static IssuedTimeParser withYearMonth(YearMonth yearMonth) {
    return new IssuedTimeParser(yearMonth);
  }

  @Override
  public IssuedTime parse(String rawText) {
    Matcher matcher = matcher(rawText, IssuedTimeRegex.regex());

    if (!matcher.find()) {
      throw new InvalidInputException("Issued time not found in report:  " + rawText);
    }

    int parsedDay = parseDay(matcher);
    int parsedHour = parseLocalTime(matcher).getHour();
    int parsedMinute = parseLocalTime(matcher).getMinute();
    YearMonth paredYearMonth = parseYearMonth(parsedDay);

    return new IssuedTime(ZonedDateTime.of(
      LocalDateTime.of(paredYearMonth.getYear(), paredYearMonth.getMonthValue(), parsedDay, parsedHour, parsedMinute),
      ZoneOffset.UTC
    ));
  }

  private int parseDay(Matcher matcher) {
    return Integer.parseInt(extractGroup(matcher, IssuedTimeRegex.DAY));
  }

  private LocalTime parseLocalTime(Matcher matcher) {
    int hour = Integer.parseInt(extractGroup(matcher, IssuedTimeRegex.HOUR));
    int minute = Integer.parseInt(extractGroup(matcher, IssuedTimeRegex.MINUTE));
    return LocalTime.of(hour, minute);
  }

  private YearMonth parseYearMonth(int parsedDay) {
    if (yearMonth != null) return yearMonth;

    YearMonth currentYearMonthUtc = YearMonth.now(ZoneOffset.UTC);
    int currentDayUtc = ZonedDateTime.now(ZoneOffset.UTC).getDayOfMonth();

    if (parsedDay > currentDayUtc + 15) {
      return currentYearMonthUtc.minusMonths(1);
    } else if (parsedDay < currentDayUtc - 15) {
      return currentYearMonthUtc.plusMonths(1);
    }

    return currentYearMonthUtc;
  }

}
