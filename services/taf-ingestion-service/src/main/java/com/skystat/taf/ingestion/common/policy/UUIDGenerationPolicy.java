package com.skystat.taf.ingestion.common.policy;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UUIDGenerationPolicy implements IdGenerationPolicy{

  @Override
  public String generate() {
    return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
  }

}
