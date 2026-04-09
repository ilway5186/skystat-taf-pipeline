package com.skystat.core.domain.spec;


import com.skystat.core.exception.BusinessException;

public abstract class AbstractSpecification<T> implements Specification<T> {
  public abstract boolean isSatisfiedBy(T t);

  public abstract void check(T t) throws BusinessException;

  public Specification<T> and(Specification<T> other) {
    return new AndSpecification<>(this, other);
  }
}
