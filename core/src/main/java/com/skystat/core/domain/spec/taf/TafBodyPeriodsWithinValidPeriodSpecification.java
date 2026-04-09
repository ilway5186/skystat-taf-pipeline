package com.skystat.core.domain.spec.taf;

import com.skystat.core.domain.entity.Taf;
import com.skystat.core.domain.spec.AbstractSpecification;
import com.skystat.core.exception.ErrorCode;
import com.skystat.core.exception.InvalidInputException;

public class TafBodyPeriodsWithinValidPeriodSpecification extends AbstractSpecification<Taf> {

  @Override
  public boolean isSatisfiedBy(Taf taf) {
    return taf.body().stream()
      .allMatch(forecastBody -> taf.validPeriod().contains(forecastBody.period()));
  }

  @Override
  public void check(Taf taf) {
    if (!isSatisfiedBy(taf)) {
      throw new InvalidInputException(ErrorCode.INVALID_INPUT, "all forecast body periods must be within validPeriod.");
    }
  }
}
