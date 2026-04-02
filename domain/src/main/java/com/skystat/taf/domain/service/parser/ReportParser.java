package com.skystat.taf.domain.service.parser;

public interface ReportParser<T> {

  T parse(String rawText);

}
