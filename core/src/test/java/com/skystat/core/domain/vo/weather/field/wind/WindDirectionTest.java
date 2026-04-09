package com.skystat.core.domain.vo.weather.field.wind;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WindDirectionTest {

  @Test
  void 고정풍향은_degree가_필수다() {
    assertThrows(IllegalArgumentException.class, () -> new WindDirection(WindDirectionType.FIXED, null));
  }

  @Test
  void 가변풍향은_degree를_가질_수_없다() {
    assertThrows(IllegalArgumentException.class, () -> new WindDirection(WindDirectionType.VARIABLE, 90.0));
  }

  @Test
  void degree는_0에서_360사이여야_한다() {
    assertThrows(IllegalArgumentException.class, () -> WindDirection.fixed(-1.0));
    assertThrows(IllegalArgumentException.class, () -> WindDirection.fixed(361.0));
  }

  @Test
  void 팩토리_메서드가_정상동작한다() {
    assertDoesNotThrow(() -> WindDirection.fixed(90.0));

    WindDirection variable = WindDirection.variable();
    assertEquals(WindDirectionType.VARIABLE, variable.type());
    assertNull(variable.degree());
  }
}
