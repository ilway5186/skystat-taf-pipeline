package com.skystat.core.domain.service.parser;

import java.time.Instant;

public interface TemporalFieldParser<T> {

  T parse(String reportText, Instant referenceInstant);

}
