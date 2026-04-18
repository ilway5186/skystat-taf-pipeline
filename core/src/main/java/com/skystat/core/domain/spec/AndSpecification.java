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
    spec1.check(t);
    spec2.check(t);
  }

}
