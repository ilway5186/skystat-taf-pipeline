package com.skystat.taf.ingestion.storage.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PublicationRequestJpaRepository extends JpaRepository<PublicationRequestEntity, Long> {

  boolean existsByRequestId(String requestId);

  Optional<PublicationRequestEntity> findByRequestId(String requestId);

}
