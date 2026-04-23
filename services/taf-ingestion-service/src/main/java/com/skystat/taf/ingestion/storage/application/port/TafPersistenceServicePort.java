package com.skystat.taf.ingestion.storage.application.port;

import com.skystat.taf.ingestion.storage.domain.vo.StoredTaf;

import java.util.Optional;

public interface TafPersistenceServicePort {

  boolean existsByTafId(String tafId);

  StoredTaf insert(StoredTaf taf);

  Optional<StoredTaf> findByTafId(String tafId);

}
