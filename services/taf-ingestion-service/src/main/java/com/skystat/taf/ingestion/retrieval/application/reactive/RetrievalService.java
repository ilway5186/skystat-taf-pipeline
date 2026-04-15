package com.skystat.taf.ingestion.retrieval.application.reactive;

import com.skystat.taf.ingestion.retrieval.application.dto.RetrievalServiceResult;
import com.skystat.taf.ingestion.retrieval.domain.Retrieval;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface RetrievalService {

  Mono<RetrievalServiceResult> retrieve(String icao);
  Flux<RetrievalServiceResult> retrieve(List<String> icao, int concurrency);

  Mono<RetrievalServiceResult> retry(Retrieval retrieval);
  Flux<RetrievalServiceResult> retry(List<Retrieval> retrievals, int concurrency);

}
