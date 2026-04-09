package com.skystat.core.domain.vo.weather.field.cloud;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CloudTest {

  @Test
  void 필수필드는_null일_수_없다() {
    assertThrows(NullPointerException.class, () -> new Cloud(null, CloudCoverage.BROKEN, 3000.0, CloudType.NONE));
    assertThrows(NullPointerException.class, () -> new Cloud("BKN030", null, 3000.0, CloudType.NONE));
  }

  @Test
  void 고도가_필요한_피복은_altitude가_필수다() {
    assertThrows(IllegalArgumentException.class, () -> new Cloud("BKN", CloudCoverage.BROKEN, null, CloudType.NONE));
  }

  @Test
  void 고도가_없는_피복은_altitude를_가질_수_없다() {
    assertThrows(IllegalArgumentException.class, () -> new Cloud("NSC", CloudCoverage.NO_SIGNIFICANT_CLOUD, 3000.0, CloudType.NONE));
  }

  @Test
  void VV는_null_altitude를_허용한다() {
    assertDoesNotThrow(() -> new Cloud("VV///", CloudCoverage.VERTICAL_VISIBILITY, null, CloudType.NONE));
  }

  @Test
  void 고도상한을_넘을_수_없다() {
    assertThrows(IllegalArgumentException.class, () -> new Cloud("BKN999", CloudCoverage.BROKEN, 100000.0, CloudType.NONE));
  }
}
