package com.skystat.taf.ingestion.storage.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TafStorageJpaRepository extends JpaRepository<TafStorageEntity, Long> {

  boolean existsByTafId(String tafId);

  Optional<TafStorageEntity> findByTafId(String tafId);

}
