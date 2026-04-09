package com.skystat.core.domain.service.parser.regex.body;

import com.skystat.core.domain.support.DomainFixtures;
import com.skystat.core.domain.vo.taf.ChangeIndicator;
import com.skystat.core.exception.ParsingException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ForecastBodyResolverTest {

  private final ForecastBodyResolver resolver = new ForecastBodyResolver();

  @Test
  void REPLACE정책은_필수필드가_없으면_예외다() {
    ParsedForecastSection section = new ParsedForecastSection(
      "FM091900 9999",
      ChangeIndicator.FM,
      DomainFixtures.period("2026-04-09T19:00:00Z", "2026-04-09T19:00:00Z"),
      Optional.empty(),
      Optional.empty(),
      List.of(),
      List.of(),
      false,
      false
    );

    assertThrows(ParsingException.class, () -> resolver.resolve(section, null, section.forecastRaw()));
  }

  @Test
  void MERGE정책은_참조필드로_누락값을_보완한다() {
    ReferenceFields referenceFields = new ReferenceFields(
      DomainFixtures.wind(190, 8, null),
      DomainFixtures.visibilityMeter(2000),
      List.of(DomainFixtures.rain()),
      List.of(DomainFixtures.brokenCloud(1000))
    );
    ParsedForecastSection section = new ParsedForecastSection(
      "TEMPO 0914/0918 0800",
      ChangeIndicator.TEMPO,
      DomainFixtures.period("2026-04-09T14:00:00Z", "2026-04-09T18:00:00Z"),
      Optional.empty(),
      Optional.of(DomainFixtures.visibilityMeter(800)),
      List.of(),
      List.of(),
      false,
      false
    );

    var body = resolver.resolve(section, referenceFields, section.forecastRaw());

    assertEquals(referenceFields.wind(), body.wind());
    assertEquals(800.0, body.visibility().value());
    assertEquals(referenceFields.weathers(), body.weathers());
    assertEquals(referenceFields.clouds(), body.clouds());
  }
}
