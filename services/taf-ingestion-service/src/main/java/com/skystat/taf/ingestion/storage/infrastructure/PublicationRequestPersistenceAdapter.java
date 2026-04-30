package com.skystat.taf.ingestion.storage.infrastructure;

import com.skystat.taf.ingestion.common.annotation.Adapter;
import com.skystat.taf.ingestion.common.exception.IngestionErrorCode;
import com.skystat.taf.ingestion.common.exception.IngestionException;
import com.skystat.taf.ingestion.storage.application.port.PublicationRequestPersistencePort;
import com.skystat.taf.ingestion.storage.domain.PublicationRequest;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@Adapter
@RequiredArgsConstructor
public class PublicationRequestPersistenceAdapter implements PublicationRequestPersistencePort {

  private final PublicationRequestJpaRepository jpaRepository;
  private final PublicationRequestJdbcRepository jdbcRepository;

  @Override
  public boolean existsByRequestId(String requestId) {
    return jpaRepository.existsByRequestId(requestId);
  }

  @Override
  public boolean existsAnyByRequestIds(List<String> requestIds) {
    return jdbcRepository.existsAnyByRequestIds(requestIds);
  }

  @Override
  public boolean existsAllByRequestIds(List<String> requestIds) {
    return jdbcRepository.existsAllByRequestIds(requestIds);
  }

  @Override
  public PublicationRequest insert(PublicationRequest request) {
    PublicationRequestEntity entity = PublicationRequestEntity.from(request);
    PublicationRequestEntity saved = jpaRepository.save(entity);
    return saved.toDomain();
  }

  @Override
  public PublicationRequest update(PublicationRequest request) {
    PublicationRequestEntity existing = jpaRepository.findByRequestId(request.requestId())
      .orElseThrow(() -> new IngestionException(IngestionErrorCode.RESOURCE_NOT_FOUND));

    PublicationRequestEntity entity = PublicationRequestEntity.from(request, existing.id());
    PublicationRequestEntity saved = jpaRepository.save(entity);
    return saved.toDomain();
  }

  @Override
  public List<PublicationRequest> insertAll(List<PublicationRequest> requests) {
    List<PublicationRequestEntity> entities = requests.stream()
      .map(PublicationRequestEntity::from)
      .toList();

    return jpaRepository.saveAll(entities).stream()
      .map(PublicationRequestEntity::toDomain)
      .toList();
  }

  @Override
  public Optional<PublicationRequest> findByRequestId(String requestId) {
    return jpaRepository.findByRequestId(requestId)
      .map(PublicationRequestEntity::toDomain);
  }

}
