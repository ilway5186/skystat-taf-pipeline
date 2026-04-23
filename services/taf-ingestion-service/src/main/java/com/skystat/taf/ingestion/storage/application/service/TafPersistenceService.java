package com.skystat.taf.ingestion.storage.application.service;

import com.skystat.core.domain.entity.Taf;
import com.skystat.taf.ingestion.storage.domain.vo.StoredTaf;

import java.time.Instant;
import java.util.Optional;

public interface TafPersistenceService {

  boolean existsByTafId(String tafId);

  StoredTaf insert(StoredTaf storedTaf);

}
