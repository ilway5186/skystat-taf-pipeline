package com.skystat.core.domain.service.parser.regex.header;

import com.skystat.core.exception.ErrorCode;
import com.skystat.core.domain.service.parser.regex.core.RegexFieldParser;

import com.skystat.core.domain.vo.weather.field.IssuedTime;import com.skystat.core.exception.ParsingException;

import java.time.*;
import java.util.regex.Matcher;

public class IssuedTimeParser extends RegexFieldParser<IssuedTime> {

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
  public IssuedTime parse(String reportText) {
    Matcher matcher = matcher(reportText, IssuedTimeRegex.regex());

    if (!matcher.find()) {
      throw new ParsingException(ErrorCode.MISSING_ISSUED_TIME, reportText);
    }

    int parsedDay = parseDay(matcher);
    int parsedHour = parseLocalTime(matcher).getHour();
    int parsedMinute = parseLocalTime(matcher).getMinute();
    YearMonth paredYearMonth = parseYearMonth(parsedDay);

    Instant issuedTime = ZonedDateTime.of(
      LocalDateTime.of(paredYearMonth.getYear(), paredYearMonth.getMonthValue(), parsedDay, parsedHour, parsedMinute),
      ZoneOffset.UTC
    ).toInstant();

    return new IssuedTime(issuedTime);
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
