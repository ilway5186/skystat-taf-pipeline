package com.skystat.core.domain.vo.weather.field;

import com.skystat.core.domain.vo.weather.unit.LengthUnit;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VisibilityTest {

  @Test
  void 단위는_null일_수_없다() {
    assertThrows(NullPointerException.class, () -> new Visibility(1000, null, false, false));
  }

  @Test
  void 값은_음수일_수_없다() {
    assertThrows(IllegalArgumentException.class, () -> new Visibility(-1, LengthUnit.METER, false, false));
  }

  @Test
  void P6SM_팩토리는_정상값을_생성한다() {
    Visibility visibility = Visibility.p6sm();

    assertEquals(LengthUnit.SM, visibility.unit());
    assertTrue(visibility.isP6SM());
    assertFalse(visibility.isCavok());
  }

  @Test
  void CAVOK_팩토리는_정상값을_생성한다() {
    Visibility visibility = Visibility.cavok();

    assertEquals(Visibility.MAX_VISIBILITY_METER, visibility.value());
    assertEquals(LengthUnit.METER, visibility.unit());
    assertFalse(visibility.isP6SM());
    assertTrue(visibility.isCavok());
  }
}
