package com.skystat.core.domain.spec;

import com.skystat.core.exception.BusinessException;

public interface Specification<T> {

  boolean isSatisfiedBy(T t);
  void check(T t) throws BusinessException;
  Specification<T> and(Specification<T> other);

}
