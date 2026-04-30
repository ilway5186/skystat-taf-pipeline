package com.skystat.taf.ingestion.storage.application.port;

import com.skystat.taf.ingestion.storage.domain.vo.TafStorage;

import java.util.Optional;

public interface TafPersistenceServicePort {

  boolean existsByTafId(String tafId);

  TafStorage insert(TafStorage tafStorage);

  Optional<TafStorage> findByTafId(String tafId);

}
