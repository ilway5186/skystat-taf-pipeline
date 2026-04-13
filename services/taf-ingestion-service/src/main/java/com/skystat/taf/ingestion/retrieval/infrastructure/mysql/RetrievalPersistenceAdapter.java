package com.skystat.taf.ingestion.retrieval.infrastructure.mysql;

import com.skystat.taf.ingestion.common.annotation.Adapter;
import com.skystat.taf.ingestion.retrieval.application.RetrievalPersistencePort;
import com.skystat.taf.ingestion.retrieval.domain.Retrieval;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Adapter
@RequiredArgsConstructor
public class RetrievalPersistenceAdapter implements RetrievalPersistencePort {

  private final RetrievalR2dbcRepository retrievalR2dbcRepository;

  @Override
  public Mono<Retrieval> save(Retrieval retrieval) {
    return retrievalR2dbcRepository.findByGroupIdAndAttemptCount(retrieval.groupId(), retrieval.attemptCount())
      .map(existing -> RetrievalEntity.from(retrieval, existing.id()))
      .switchIfEmpty(Mono.fromSupplier(() -> RetrievalEntity.from(retrieval, null)))
      .flatMap(retrievalR2dbcRepository::save)
      .map(RetrievalEntity::toDomain);
  }

  @Override
  public Flux<Retrieval> findAllByGroupId(String groupId) {
    return retrievalR2dbcRepository.findAllByGroupIdOrderByAttemptCountAsc(groupId)
      .map(RetrievalEntity::toDomain);
  }

}
