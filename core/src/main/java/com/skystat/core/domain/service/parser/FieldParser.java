package com.skystat.core.domain.service.parser;

import java.time.Instant;

public interface FieldParser<T> {

  T parse(String reportText);

}
