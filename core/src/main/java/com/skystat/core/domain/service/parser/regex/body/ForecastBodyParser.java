package com.skystat.core.domain.service.parser.regex.body;

import com.skystat.core.domain.service.parser.TemporalFieldParser;
import com.skystat.core.domain.service.parser.regex.forecast.ChangeIndicatorParser;
import com.skystat.core.domain.service.parser.regex.forecast.ChangeIndicatorParser.ChangeIndicatorMatch;
import com.skystat.core.domain.service.parser.regex.forecast.CloudParser;
import com.skystat.core.domain.service.parser.regex.forecast.ForecastPeriodParser;
import com.skystat.core.domain.service.parser.regex.forecast.VisibilityParser;
import com.skystat.core.domain.service.parser.regex.forecast.WeatherParser;
import com.skystat.core.domain.service.parser.regex.forecast.WindParser;
import com.skystat.core.domain.vo.taf.ChangeIndicator;
import com.skystat.core.domain.vo.taf.ForecastBody;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class ForecastBodyParser implements TemporalFieldParser<List<ForecastBody>> {

  private final ChangeIndicatorParser changeIndicatorParser;
  private final ForecastSectionParser forecastSectionParser;
  private final ForecastBodyResolver forecastBodyResolver;

  public ForecastBodyParser(
    ChangeIndicatorParser changeIndicatorParser,
    ForecastPeriodParser forecastPeriodParser,
    WindParser windParser,
    VisibilityParser visibilityParser,
    WeatherParser weatherParser,
    CloudParser cloudParser
  ) {
    this(
      changeIndicatorParser,
      new ForecastSectionParser(forecastPeriodParser, windParser, visibilityParser, weatherParser, cloudParser),
      new ForecastBodyResolver()
    );
  }

  @Override
  public List<ForecastBody> parse(String reportText, Instant referenceInstant) {
    List<ForecastBody> forecastBodies = new ArrayList<>();
    ReferenceFields referenceFields = null;

    for (ForecastSection section : splitSections(reportText)) {
      ParsedForecastSection parsedSection = forecastSectionParser.parse(section, referenceInstant);
      ForecastBody body = forecastBodyResolver.resolve(parsedSection, referenceFields, section.sectionRaw());
      forecastBodies.add(body);

      if (section.indicator().referenceable()) {
        referenceFields = ReferenceFields.from(body);
      }
    }

    return List.copyOf(forecastBodies);
  }

  private List<ForecastSection> splitSections(String reportText) {
    List<ChangeIndicatorMatch> indicatorMatches = changeIndicatorParser.parse(reportText);
    if (indicatorMatches.isEmpty()) {
      return List.of(new ForecastSection(ChangeIndicator.HEADER, reportText.trim()));
    }

    List<ForecastSection> sections = new ArrayList<>();

    ChangeIndicatorMatch firstMatch = indicatorMatches.getFirst();
    if (firstMatch.startIndex() > 0) {
      sections.add(new ForecastSection(
        ChangeIndicator.HEADER,
        reportText.substring(0, firstMatch.startIndex()).trim()
      ));
    }

    for (int i=0; i<indicatorMatches.size(); i++) {
      ChangeIndicatorMatch currentMatch = indicatorMatches.get(i);
      int endIndex = i + 1 < indicatorMatches.size()
        ? indicatorMatches.get(i + 1).startIndex()
        : reportText.length();

      sections.add(new ForecastSection(
        currentMatch.indicator(),
        reportText.substring(currentMatch.startIndex(), endIndex).trim()
      ));
    }

    return List.copyOf(sections);
  }
}
