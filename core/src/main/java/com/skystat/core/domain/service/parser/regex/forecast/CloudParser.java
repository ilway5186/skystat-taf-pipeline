package com.skystat.core.domain.service.parser.regex.forecast;

import com.skystat.core.domain.service.parser.FieldParser;
import com.skystat.core.domain.service.parser.regex.core.RegexParsingSupport;
import com.skystat.core.domain.vo.weather.field.cloud.Cloud;
import com.skystat.core.domain.vo.weather.field.cloud.CloudCoverage;
import com.skystat.core.domain.vo.weather.field.cloud.CloudType;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

public class CloudParser extends RegexParsingSupport implements FieldParser<List<Cloud>> {

  @Override
  public List<Cloud> parse(String reportText) {
    Matcher matcher = matcher(reportText, CloudRegex.regex());
    List<Cloud> clouds = new ArrayList<>();

    while (matcher.find()) {
      String coverageMatch = matcher.group(CloudRegex.COVERAGE.groupName());
      String altitudeMatch = matcher.group(CloudRegex.ALTITUDE.groupName());
      String typeMatch = matcher.group(CloudRegex.TYPE.groupName());

      CloudCoverage coverage = CloudCoverage.fromSymbol(coverageMatch);
      CloudType type = typeMatch != null ? CloudType.fromSymbol(typeMatch) : CloudType.NONE;

      Double altitude = null;
      if (altitudeMatch != null && !altitudeMatch.contains("/")) {
        altitude = Double.parseDouble(altitudeMatch)*100;
      }

      clouds.add(new Cloud(matcher.group(0), coverage, altitude, type));
    }

    return clouds;
  }

}
