package com.skystat.core.domain.service.parser;

public interface FieldParser<T> {

  T parse(String reportText);

}
