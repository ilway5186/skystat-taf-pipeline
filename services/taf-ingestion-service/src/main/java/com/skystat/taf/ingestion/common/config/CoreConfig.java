package com.skystat.taf.ingestion.common.config;

import com.skystat.core.domain.service.parser.TafParser;
import com.skystat.core.domain.service.parser.regex.TafRegexParser;
import com.skystat.core.domain.service.parser.regex.body.ForecastBodyParser;
import com.skystat.core.domain.service.parser.regex.body.ForecastBodyResolver;
import com.skystat.core.domain.service.parser.regex.body.ForecastSectionParser;
import com.skystat.core.domain.service.parser.regex.forecast.*;
import com.skystat.core.domain.service.parser.regex.header.IssuedTimeParser;
import com.skystat.core.domain.service.parser.regex.header.ReportTypeParser;
import com.skystat.core.domain.service.parser.regex.header.StationIcaoParser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CoreConfig {

  @Bean
  public TafParser tafParser() {
    return new TafRegexParser(
      new ForecastBodyParser(
        new ChangeIndicatorParser(),
        new ForecastSectionParser(
          new ForecastPeriodParser(),
          new WindParser(),
          new VisibilityParser(),
          new WeatherParser(),
          new CloudParser()
        ),
        new ForecastBodyResolver()
      ),
      new ReportTypeParser(),
      new IssuedTimeParser(),
      new StationIcaoParser()
    );
  }

}
