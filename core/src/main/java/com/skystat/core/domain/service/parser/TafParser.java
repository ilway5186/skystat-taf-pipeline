package com.skystat.core.domain.service.parser;

import com.skystat.core.domain.entity.Taf;

import java.time.Instant;

public interface TafParser {

  Taf parse(String reportText, Instant referenceInstant);

}
