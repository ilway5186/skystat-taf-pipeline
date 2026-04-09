package com.skystat.core.domain.spec.taf;

import com.skystat.core.domain.entity.Taf;
import com.skystat.core.domain.spec.AbstractSpecification;
import com.skystat.core.domain.vo.taf.ChangeIndicator;
import com.skystat.core.domain.vo.taf.ForecastBody;
import com.skystat.core.exception.ErrorCode;
import com.skystat.core.exception.InvalidInputException;

import java.util.List;
import java.util.Objects;

public class TafBodyStructureSpecification extends AbstractSpecification<Taf> {

  @Override
  public boolean isSatisfiedBy(Taf taf) {
    List<ForecastBody> bodies = taf.body();
    long headerCount = bodies.stream()
      .filter(forecastBody -> forecastBody.indicator() == ChangeIndicator.HEADER)
      .count();

    return !bodies.isEmpty()
      && bodies.stream().noneMatch(Objects::isNull)
      && headerCount == 1;
  }

  @Override
  public void check(Taf taf) {
    List<ForecastBody> bodies = taf.body();

    if (bodies.isEmpty()) {
      throw new InvalidInputException(ErrorCode.INVALID_INPUT, "body cannot be empty.");
    }
    if (bodies.stream().anyMatch(Objects::isNull)) {
      throw new InvalidInputException(ErrorCode.INVALID_INPUT, "body cannot contain null elements.");
    }

    long headerCount = bodies.stream()
      .filter(forecastBody -> forecastBody.indicator() == ChangeIndicator.HEADER)
      .count();

    if (headerCount != 1) {
      throw new InvalidInputException(ErrorCode.INVALID_INPUT, "body must contain exactly one HEADER forecast.");
    }
  }
}
