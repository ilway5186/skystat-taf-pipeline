package com.skystat.taf.domain.condition;

import lombok.RequiredArgsConstructor;

import java.util.function.BiPredicate;

@RequiredArgsConstructor
public enum Comparison {

  LT((v,t) -> v < t),
  LTE((v,t) -> v <= t),
  EQ((v,t) -> Math.abs(v - t) < 0.00001),
  NE((v,t) -> Math.abs(v - t) >= 0.00001),
  GT((v,t) -> v > t),
  GTE((v,t) -> v >= t);

  private final BiPredicate<Double, Double> op;

  public boolean test(double value, double threshold) {
    return op.test(value, threshold);
  }

  public boolean test(int value, int threshold) {
    return op.test((double) value, (double) threshold);
  }

}
