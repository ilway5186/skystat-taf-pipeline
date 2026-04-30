package com.skystat.taf.ingestion.storage.application.service;

import com.skystat.taf.ingestion.storage.domain.vo.TafStorage;

public interface TafPersistenceService {

  boolean existsByTafId(String tafId);

  TafStorage insert(TafStorage tafStorage);

}
