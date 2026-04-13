package com.skystat.taf.ingestion.retrieval.infrastructure.mysql;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RetrievalR2dbcRepository extends ReactiveCrudRepository<RetrievalEntity, Long> {

  Mono<RetrievalEntity> findByGroupIdAndAttemptCount(String groupId, int attemptCount);

  Flux<RetrievalEntity> findAllByGroupIdOrderByAttemptCountAsc(String groupId);

}
