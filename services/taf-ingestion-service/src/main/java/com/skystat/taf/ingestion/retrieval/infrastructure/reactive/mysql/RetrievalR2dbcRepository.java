package com.skystat.taf.ingestion.retrieval.infrastructure.reactive.mysql;

import org.springframework.context.annotation.Profile;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Profile("reactive")
public interface RetrievalR2dbcRepository extends ReactiveCrudRepository<RetrievalEntity, Long> {

  Mono<RetrievalEntity> findByGroupIdAndAttemptCount(String groupId, int attemptCount);

  Flux<RetrievalEntity> findAllByGroupIdOrderByAttemptCountAsc(String groupId);

}
