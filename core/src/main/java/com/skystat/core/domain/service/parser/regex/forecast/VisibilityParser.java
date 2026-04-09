package com.skystat.core.domain.service.parser.regex.forecast;

import com.skystat.core.domain.vo.weather.field.Visibility;
import com.skystat.core.domain.vo.weather.unit.LengthUnit;
import com.skystat.core.domain.service.parser.regex.core.RegexFieldParser;

import java.util.Optional;
import java.util.regex.Matcher;

import static com.skystat.core.domain.service.parser.regex.forecast.VisibilityRegex.*;

public class VisibilityParser extends RegexFieldParser<Optional<Visibility>> {

  @Override
  public Optional<Visibility> parse(String reportText) {
    Matcher matcher = matcher(reportText, regex());

    if (!matcher.find()) {
      return Optional.empty();
    }

    if (extractGroup(matcher, CAVOK) != null) return Optional.of(Visibility.cavok());
    if (extractGroup(matcher, P6SM) != null) return Optional.of(Visibility.p6sm());


    String visibilityGroup;

    visibilityGroup = extractGroup(matcher, IMPROPER_FRACTION_MILE);
    if (visibilityGroup != null) return Optional.of(parseImproperFraction(visibilityGroup));

    visibilityGroup = extractGroup(matcher, FRACTION_MILE);
    if (visibilityGroup != null) return Optional.of(parseFraction(visibilityGroup));

    visibilityGroup = extractGroup(matcher, MILE);
    if (visibilityGroup != null) return Optional.of(parseMile(visibilityGroup));

    visibilityGroup = extractGroup(matcher, DIGIT);
    if (visibilityGroup != null) return Optional.of(parseDigit(visibilityGroup));

    return Optional.empty();
  }

  private Visibility parseImproperFraction(String visibilityGroup) {
    String cleanText = visibilityGroup.replace("SM", "");
    String[] parts = cleanText.split(" ");
    double whole = Double.parseDouble(parts[0]);
    double fraction = calculateFraction(parts[1]);
    return new Visibility(whole + fraction, LengthUnit.SM, false, false);
  }

  private Visibility parseFraction(String text) {
    String cleanText = text.replace("SM", "");
    return new Visibility(calculateFraction(cleanText), LengthUnit.SM, false, false);
  }

  private Visibility parseMile(String text) {
    String cleanText = text.replace("SM", "");
    return new Visibility(Double.parseDouble(cleanText), LengthUnit.SM, false, false);
  }

  private Visibility parseDigit(String text) {
    return new Visibility(Double.parseDouble(text), LengthUnit.METER, false, false);
  }

  private double calculateFraction(String fractionText) {
    String[] parts = fractionText.split("/");
    double numerator = Double.parseDouble(parts[0]);
    double denominator = Double.parseDouble(parts[1]);
    return numerator / denominator;
  }

}
