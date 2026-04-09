package com.skystat.core.domain.service.parser.regex.forecast;

import com.skystat.core.domain.vo.weather.field.cloud.Cloud;
import com.skystat.core.domain.vo.weather.field.cloud.CloudCoverage;
import com.skystat.core.domain.vo.weather.field.cloud.CloudType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CloudParserTest {

  private final CloudParser parser = new CloudParser();

  @Test
  void 구름목록을_파싱한다() {
    List<Cloud> clouds = parser.parse("FEW010 BKN030 OVC070");

    assertEquals(3, clouds.size());
    assertEquals(CloudCoverage.FEW, clouds.get(0).coverage());
    assertEquals(1000.0, clouds.get(0).altitude());
    assertEquals(CloudCoverage.BROKEN, clouds.get(1).coverage());
  }

  @Test
  void 구름타입을_파싱한다() {
    List<Cloud> clouds = parser.parse("BKN030CB");

    assertEquals(1, clouds.size());
    assertEquals(CloudType.CUMULONIMBUS, clouds.getFirst().type());
  }

  @Test
  void 구름정보가_없으면_빈리스트다() {
    assertEquals(List.of(), parser.parse("09010KT 9999 -RA"));
  }
}
