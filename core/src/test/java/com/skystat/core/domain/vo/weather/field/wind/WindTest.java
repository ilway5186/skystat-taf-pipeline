package com.skystat.core.domain.vo.weather.field.wind;

import com.skystat.core.domain.vo.weather.unit.SpeedUnit;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WindTest {

  @Test
  void 풍향과_단위는_null일_수_없다() {
    assertThrows(NullPointerException.class, () -> new Wind(null, 10.0, null, SpeedUnit.KT));
    assertThrows(NullPointerException.class, () -> new Wind(WindDirection.fixed(90.0), 10.0, null, null));
  }

  @Test
  void 속도와_돌풍은_음수일_수_없다() {
    assertThrows(IllegalArgumentException.class, () -> new Wind(WindDirection.fixed(90.0), -1.0, null, SpeedUnit.KT));
    assertThrows(IllegalArgumentException.class, () -> new Wind(WindDirection.fixed(90.0), 10.0, -1.0, SpeedUnit.KT));
  }

  @Test
  void 속도와_돌풍은_상한을_넘을_수_없다() {
    assertThrows(IllegalArgumentException.class, () -> new Wind(WindDirection.fixed(90.0), 400.0, null, SpeedUnit.KT));
    assertThrows(IllegalArgumentException.class, () -> new Wind(WindDirection.fixed(90.0), 10.0, 400.0, SpeedUnit.KT));
  }

  @Test
  void peakSpeed를_정상적으로_반환한다() {
    assertNull(new Wind(WindDirection.fixed(90.0), null, null, SpeedUnit.KT).peakSpeed());
    assertEquals(10.0, new Wind(WindDirection.fixed(90.0), 10.0, null, SpeedUnit.KT).peakSpeed());
    assertEquals(20.0, new Wind(WindDirection.fixed(90.0), null, 20.0, SpeedUnit.KT).peakSpeed());
    assertEquals(20.0, new Wind(WindDirection.fixed(90.0), 10.0, 20.0, SpeedUnit.KT).peakSpeed());
  }
}
