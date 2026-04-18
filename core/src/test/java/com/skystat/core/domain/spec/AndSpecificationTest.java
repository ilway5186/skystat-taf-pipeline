package com.skystat.core.domain.spec;

import com.skystat.core.exception.InvalidInputException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AndSpecificationTest {

  @Test
  void check는_만족하지_않는_첫번째_명세의_예외를_그대로_전파해야한다() {
    Specification<String> specification = invalidOnCheck("first specification failed.")
      .and(valid());

    InvalidInputException exception = assertThrows(
      InvalidInputException.class,
      () -> specification.check("TAF")
    );

    assertEquals("first specification failed.", exception.getMessage());
  }

  @Test
  void check는_첫번째_명세가_만족되면_두번째_명세의_예외를_그대로_전파해야한다() {
    Specification<String> specification = valid()
      .and(invalidOnCheck("second specification failed."));

    InvalidInputException exception = assertThrows(
      InvalidInputException.class,
      () -> specification.check("TAF")
    );

    assertEquals("second specification failed.", exception.getMessage());
  }

  @Test
  void check는_두_명세의_check를_순서대로_호출해야한다() {
    CheckRecordingSpecification first = new CheckRecordingSpecification(true);
    CheckRecordingSpecification second = new CheckRecordingSpecification(true);
    Specification<String> specification = first.and(second);

    specification.check("TAF");

    assertTrue(first.checked);
    assertTrue(second.checked);
  }

  @Test
  void isSatisfiedBy는_두_명세의_만족여부를_조합해야한다() {
    Specification<String> specification = valid()
      .and(unsatisfied());

    assertFalse(specification.isSatisfiedBy("TAF"));
  }

  private static AbstractSpecification<String> valid() {
    return new AbstractSpecification<>() {
      @Override
      public boolean isSatisfiedBy(String value) {
        return true;
      }

      @Override
      public void check(String value) {
      }
    };
  }

  private static AbstractSpecification<String> unsatisfied() {
    return new AbstractSpecification<>() {
      @Override
      public boolean isSatisfiedBy(String value) {
        return false;
      }

      @Override
      public void check(String value) {
      }
    };
  }

  private static AbstractSpecification<String> invalidOnCheck(String message) {
    return new AbstractSpecification<>() {
      @Override
      public boolean isSatisfiedBy(String value) {
        return true;
      }

      @Override
      public void check(String value) {
        throw new InvalidInputException(message);
      }
    };
  }

  private static class CheckRecordingSpecification extends AbstractSpecification<String> {

    private final boolean satisfied;
    private boolean checked;

    private CheckRecordingSpecification(boolean satisfied) {
      this.satisfied = satisfied;
    }

    @Override
    public boolean isSatisfiedBy(String value) {
      return satisfied;
    }

    @Override
    public void check(String value) {
      checked = true;
    }

  }

}
