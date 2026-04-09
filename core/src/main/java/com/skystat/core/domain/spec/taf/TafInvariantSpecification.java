package com.skystat.core.domain.spec.taf;

import com.skystat.core.domain.entity.Taf;
import com.skystat.core.domain.spec.AbstractSpecification;
import com.skystat.core.domain.spec.Specification;

public class TafInvariantSpecification extends AbstractSpecification<Taf> {

  private final Specification<Taf> delegate =
    new TafBodyStructureSpecification()
      .and(new TafHeaderPeriodConsistencySpecification())
      .and(new TafBodyPeriodsWithinValidPeriodSpecification())
      .and(new TafTemperatureTimesWithinValidPeriodSpecification());

  @Override
  public boolean isSatisfiedBy(Taf taf) {
    return delegate.isSatisfiedBy(taf);
  }

  @Override
  public void check(Taf taf) {
    ((AbstractSpecification<Taf>) delegate).check(taf);
  }
}
