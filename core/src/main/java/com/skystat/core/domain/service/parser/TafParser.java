package com.skystat.core.domain.service.parser;

import com.skystat.core.domain.entity.Taf;

public interface TafParser {

  Taf parse(String reportText);

}
