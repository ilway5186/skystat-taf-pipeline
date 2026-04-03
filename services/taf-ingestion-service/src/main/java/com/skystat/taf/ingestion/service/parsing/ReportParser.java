package com.skystat.taf.ingestion.service.parsing;

public interface ReportParser<T> {

  T parse(String rawText);

}
