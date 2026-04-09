package com.skystat.core.domain.spec.taf;

import com.skystat.core.domain.entity.Taf;
import com.skystat.core.domain.spec.AbstractSpecification;
import com.skystat.core.exception.ErrorCode;
import com.skystat.core.exception.InvalidInputException;

import java.util.Objects;

public class TafTemperatureTimesWithinValidPeriodSpecification extends AbstractSpecification<Taf> {

  @Override
  public boolean isSatisfiedBy(Taf taf) {
    return taf.temperatures().stream()
      .noneMatch(Objects::isNull)
      && taf.temperatures().stream().allMatch(temperature -> taf.validPeriod().contains(temperature.time()));
  }

  @Override
  public void check(Taf taf) {
    if (taf.temperatures().stream().anyMatch(Objects::isNull)) {
      throw new InvalidInputException(ErrorCode.INVALID_INPUT, "temperatures cannot contain null elements.");
    }
    if (!taf.temperatures().stream().allMatch(temperature -> taf.validPeriod().contains(temperature.time()))) {
      throw new InvalidInputException(ErrorCode.INVALID_INPUT, "all forecast temperatures must be within validPeriod.");
    }
  }
}
