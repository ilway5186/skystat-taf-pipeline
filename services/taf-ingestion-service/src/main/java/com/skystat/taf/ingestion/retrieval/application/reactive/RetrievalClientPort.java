package com.skystat.taf.ingestion.retrieval.application.reactive;

import com.skystat.taf.ingestion.retrieval.application.dto.RetrievalResult;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface RetrievalClientPort {

  Mono<RetrievalResult> retrieve(String icao);
  Flux<RetrievalResult> retrieve(List<String> icaos, int concurrency);

}
