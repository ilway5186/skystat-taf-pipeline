package com.skystat.taf.ingestion.retrieval.application.reactive;

import com.skystat.taf.ingestion.retrieval.application.dto.RetrievalResult;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@Profile("reactive")
@RequiredArgsConstructor
public class RetrievalClientServiceImpl implements RetrievalClientService {

  private final RetrievalClientPort retrievalClientPort;

  @Override
  public Mono<RetrievalResult> retrieve(String icao) {
    return retrievalClientPort.retrieve(icao);
  }

  @Override
  public Flux<RetrievalResult> retrieve(List<String> icaos, int concurrency) {
    return retrievalClientPort.retrieve(icaos, concurrency);
  }

}
