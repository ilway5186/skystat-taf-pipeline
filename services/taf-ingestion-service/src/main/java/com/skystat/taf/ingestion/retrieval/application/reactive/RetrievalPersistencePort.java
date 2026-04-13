package com.skystat.taf.ingestion.retrieval.application.reactive;

import com.skystat.taf.ingestion.retrieval.domain.Retrieval;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RetrievalPersistencePort {

  Mono<Retrieval> save(Retrieval retrieval);

  Flux<Retrieval> findAllByGroupId(String groupId);

}
