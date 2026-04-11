package com.skystat.taf.ingestion.retrieval.application;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface RetrievalOutputPort {

  Mono<RetrievalResult> retrieve(String icao);
  Flux<RetrievalResult> retrieve(List<String> icaos, int concurrency);

  Mono<RetrievalResult> retry(String icao);
  Flux<RetrievalResult> retry(List<String> icaos, int concurrency);

}
