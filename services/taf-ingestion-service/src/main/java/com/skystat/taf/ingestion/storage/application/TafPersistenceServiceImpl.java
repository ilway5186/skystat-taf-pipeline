package com.skystat.taf.ingestion.storage.application;

import com.skystat.taf.ingestion.common.exception.IngestionErrorCode;
import com.skystat.taf.ingestion.common.exception.IngestionException;
import com.skystat.taf.ingestion.storage.application.port.TafPersistenceServicePort;
import com.skystat.taf.ingestion.storage.application.service.TafPersistenceService;
import com.skystat.taf.ingestion.storage.domain.vo.TafStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TafPersistenceServiceImpl implements TafPersistenceService {

  private final TafPersistenceServicePort tafPersistencePort;

  @Override
  @Transactional(readOnly = true)
  public boolean existsByTafId(String tafId) {
    return tafPersistencePort.existsByTafId(tafId);
  }

  @Override
  @Transactional
  public TafStorage insert(TafStorage taf) {
    if (existsByTafId(taf.tafId())) {
      throw new IngestionException(IngestionErrorCode.DUPLICATE_RESOURCE, duplicateMessage(taf));
    }

    return tafPersistencePort.insert(taf);
  }

  private static String duplicateMessage(TafStorage taf) {
    return "Taf already exists. tafId=" + taf.tafId() + ", icao=" + taf.icao();
  }

  private static String notFoundMessage(TafStorage taf) {
    return "Taf not found. tafId=" + taf.tafId() + ", icao=" + taf.icao();
  }

}
