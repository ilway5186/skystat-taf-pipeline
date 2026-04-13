package com.skystat.taf.ingestion.retrieval.application.reactive;

import com.skystat.taf.ingestion.retrieval.domain.Retrieval;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface RetrievalService {

  Mono<Retrieval> retrieve(String icao);
  Flux<Retrieval> retrieve(List<String> icao, int concurrency);

  Mono<Retrieval> retry(Retrieval retrieval);
  Flux<Retrieval> retry(List<Retrieval> retrievals, int concurrency);

}
