package com.skystat.core.domain.service.parser.regex.forecast;

import com.skystat.core.domain.vo.taf.ChangeIndicator;
import com.skystat.core.domain.service.parser.regex.core.RegexFieldParser;
import com.skystat.core.exception.ErrorCode;
import com.skystat.core.exception.ParsingException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

public class ChangeIndicatorParser extends RegexFieldParser<List<ChangeIndicatorParser.ChangeIndicatorMatch>> {

  @Override
  public List<ChangeIndicatorMatch> parse(String reportText) {
    Matcher matcher = matcher(reportText, ChangeIndicatorRegex.regex());

    List<ChangeIndicatorMatch> result = new ArrayList<>();
    while (matcher.find()) {
      String indicatorGroup = extractGroup(matcher, ChangeIndicatorRegex.INDICATOR);
      ChangeIndicator indicator = parseIndicator(indicatorGroup);
      int startIndex = matcher.start();
      result.add(new ChangeIndicatorMatch(indicator, startIndex));
    }

    return result;
  }

  private ChangeIndicator parseIndicator(String indicatorGroup) {
    if (indicatorGroup == null || indicatorGroup.isBlank()) {
      return ChangeIndicator.HEADER;
    }
    if (indicatorGroup.startsWith(ChangeIndicator.FM.name())) {
      return ChangeIndicator.FM;
    }
    return switch (indicatorGroup.replace(' ', '_')) {
      case "HEADER" -> ChangeIndicator.HEADER;
      case "BECMG" -> ChangeIndicator.BECMG;
      case "TEMPO" -> ChangeIndicator.TEMPO;
      case "INTER" -> ChangeIndicator.INTER;
      case "PROB30" -> ChangeIndicator.PROB30;
      case "PROB40" -> ChangeIndicator.PROB40;
      case "PROB30_TEMPO" -> ChangeIndicator.PROB30_TEMPO;
      case "PROB40_TEMPO" -> ChangeIndicator.PROB40_TEMPO;
      case "NONE" -> ChangeIndicator.NONE;
      default -> throw new ParsingException(ErrorCode.INVALID_CHANGE_INDICATOR, indicatorGroup);
    };
  }

  public record ChangeIndicatorMatch(ChangeIndicator indicator, int startIndex) {};

}
