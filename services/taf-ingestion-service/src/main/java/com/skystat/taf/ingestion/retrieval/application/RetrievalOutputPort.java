package com.skystat.taf.ingestion.retrieval.application;

import com.skystat.taf.ingestion.retrieval.domain.Retrieval;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface RetrievalOutputPort {

  Mono<Retrieval> retrieve(Retrieval retrieval);
  Flux<Retrieval> retrieve(List<Retrieval> retrievals, int concurrency);

  Mono<Retrieval> retry(Retrieval retrieval);
  Flux<Retrieval> retry(List<Retrieval> retrievals, int concurrency);

}
