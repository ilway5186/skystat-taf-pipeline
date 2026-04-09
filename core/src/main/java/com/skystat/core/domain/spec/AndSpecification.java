package com.skystat.core.domain.spec;

import com.skystat.core.exception.BusinessException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AndSpecification<T> extends AbstractSpecification<T> {

  private final Specification<T> spec1;
  private final Specification<T> spec2;

  @Override
  public boolean isSatisfiedBy(T t) {
    return spec1.isSatisfiedBy(t) && spec2.isSatisfiedBy(t);
  }

  @Override
  public void check(T t) throws BusinessException {
    if (spec1 instanceof AbstractSpecification<T> abstractSpec1) {
      abstractSpec1.check(t);
    } else if (!spec1.isSatisfiedBy(t)) {
      throw new IllegalStateException("First specification is not satisfied.");
    }

    if (spec2 instanceof AbstractSpecification<T> abstractSpec2) {
      abstractSpec2.check(t);
    } else if (!spec2.isSatisfiedBy(t)) {
      throw new IllegalStateException("Second specification is not satisfied.");
    }
  }

}
