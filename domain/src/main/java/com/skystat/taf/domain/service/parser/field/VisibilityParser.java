package com.skystat.taf.domain.service.parser.field;

import com.skystat.taf.domain.exception.BusinessException;
import com.skystat.taf.domain.exception.ErrorCode;
import com.skystat.taf.domain.exception.InvalidInputException;
import com.skystat.taf.domain.service.parser.ReportRegexParser;
import com.skystat.taf.domain.vo.weather.field.Visibility;
import com.skystat.taf.domain.vo.weather.unit.LengthUnit;

import java.util.regex.Matcher;

import static com.skystat.taf.domain.service.parser.field.VisibilityRegex.*;

public class VisibilityParser extends ReportRegexParser<Visibility> {

  @Override
  public Visibility parse(String rawText) {
    Matcher matcher = matcher(rawText, regex());

    if (!matcher.find()) {
      throw new InvalidInputException("Visibility not found in report: " + rawText);
    }

    if (extractGroup(matcher, CAVOK) != null) {
      return Visibility.cavok();
    }

    if (extractGroup(matcher, P6SM) != null) {
      return Visibility.p6sm();
    }

    String visibilityGroup;

    visibilityGroup = extractGroup(matcher, IMPROPER_FRACTION_MILE);
    if (visibilityGroup != null) {
      return parseImproperFraction(visibilityGroup);
    }

    visibilityGroup = extractGroup(matcher, FRACTION_MILE);
    if (visibilityGroup != null) {
      return parseFraction(visibilityGroup);
    }

    visibilityGroup = extractGroup(matcher, MILE);
    if (visibilityGroup != null) {
      return parseMile(visibilityGroup);
    }

    visibilityGroup = extractGroup(matcher, DIGIT);
    if (visibilityGroup != null) {
      return parseDigit(visibilityGroup);
    }

    throw new BusinessException(ErrorCode.INTERNAL_ERROR, "Unreachable parsing state for visibility: " + rawText);
  }

  private Visibility parseImproperFraction(String visibilityGroup) {
    String cleanText = visibilityGroup.replace("SM", "");
    String[] parts = cleanText.split(" ");
    double whole = Double.parseDouble(parts[0]);
    double fraction = calculateFraction(parts[1]);
    return new Visibility(whole + fraction, LengthUnit.SM);
  }

  private Visibility parseFraction(String text) {
    String cleanText = text.replace("SM", "");
    return new Visibility(calculateFraction(cleanText), LengthUnit.SM);
  }

  private Visibility parseMile(String text) {
    String cleanText = text.replace("SM", "");
    return new Visibility(Double.parseDouble(cleanText), LengthUnit.SM);
  }

  private Visibility parseDigit(String text) {
    return new Visibility(Double.parseDouble(text), LengthUnit.METER);
  }

  private double calculateFraction(String fractionText) {
    String[] parts = fractionText.split("/");
    double numerator = Double.parseDouble(parts[0]);
    double denominator = Double.parseDouble(parts[1]);
    return numerator / denominator;
  }

}
