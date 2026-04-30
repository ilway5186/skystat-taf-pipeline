package com.skystat.taf.ingestion.storage.infrastructure;

import com.skystat.taf.ingestion.common.annotation.Adapter;
import com.skystat.taf.ingestion.storage.application.port.TafPersistenceServicePort;
import com.skystat.taf.ingestion.storage.domain.vo.TafStorage;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@Adapter
@RequiredArgsConstructor
public class TafPersistenceAdapter implements TafPersistenceServicePort {

  private final TafStorageJpaRepository tafStorageJpaRepository;

  @Override
  public boolean existsByTafId(String tafId) {
    return tafStorageJpaRepository.existsByTafId(tafId);
  }

  @Override
  public TafStorage insert(TafStorage tafStorage) {
    TafStorageEntity entity = TafStorageEntity.from(tafStorage);
    TafStorageEntity saved = tafStorageJpaRepository.save(entity);
    return saved.toDomain();
  }

  @Override
  public Optional<TafStorage> findByTafId(String tafId) {
    return tafStorageJpaRepository.findByTafId(tafId)
      .map(TafStorageEntity::toDomain);
  }

}
