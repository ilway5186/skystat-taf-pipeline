package com.skystat.taf.ingestion.storage.application;

import com.skystat.taf.ingestion.common.exception.IngestionErrorCode;
import com.skystat.taf.ingestion.common.exception.IngestionException;
import com.skystat.taf.ingestion.retrieval.domain.Retrieval;
import com.skystat.taf.ingestion.storage.application.port.TafPersistenceServicePort;
import com.skystat.taf.ingestion.storage.application.service.TafPersistenceService;
import com.skystat.taf.ingestion.storage.domain.vo.StoredTaf;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TafPersistenceServiceImpl implements TafPersistenceService {

  private final TafPersistenceServicePort tafPersistencePort;

  @Override
  public boolean existsByTafId(String tafId) {
    return tafPersistencePort.existsByTafId(tafId);
  }

  @Override
  public StoredTaf insert(StoredTaf taf) {
    if (existsByTafId(taf.tafId())) {
      throw new IngestionException(IngestionErrorCode.DUPLICATE_RESOURCE, duplicateMessage(taf));
    }

    return tafPersistencePort.insert(taf);
  }

  private static String duplicateMessage(StoredTaf taf) {
    return "Taf already exists. tafId=" + taf.tafId() + ", icao=" + taf.icao();
  }

  private static String notFoundMessage(StoredTaf taf) {
    return "Taf not found. tafId=" + taf.tafId() + ", icao=" + taf.icao();
  }

}
