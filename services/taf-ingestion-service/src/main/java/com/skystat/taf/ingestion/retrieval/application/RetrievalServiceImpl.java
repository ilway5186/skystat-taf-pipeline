package com.skystat.taf.ingestion.retrieval.application;

import com.skystat.taf.ingestion.common.exception.IngestionException;
import com.skystat.taf.ingestion.retrieval.domain.Retrieval;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static com.skystat.taf.ingestion.common.exception.IngestionErrorCode.INVALID_STATUS;

@Service
@RequiredArgsConstructor
public class RetrievalServiceImpl implements RetrievalService {

  private final RetrievalOutputPort retrievalOutputPort;
  private final RetrievalPersistencePort retrievalPersistencePort;

  @Override
  public Mono<Retrieval> retrieve(String icao) {
    Retrieval retrieval = createRetrieval(icao);

    return retrievalPersistencePort.save(retrieval)
      .flatMap(savedRetrieval -> retrievalOutputPort
        .retrieve(savedRetrieval.icao())
        .map(result -> complete(savedRetrieval, result))
      )
      .flatMap(retrievalPersistencePort::save);
  }

  @Override
  public Flux<Retrieval> retrieve(List<String> icao, int concurrency) {
    List<Retrieval> retrievals = new HashSet<>(icao).stream()
      .map(this::createRetrieval).toList();

    return Flux.fromIterable(retrievals)
      .flatMap(retrievalPersistencePort::save, Math.max(1, concurrency))
      .collectList()
      .flatMapMany(savedRetrievals -> {
          List<String> icaos = savedRetrievals.stream().map(Retrieval::icao).toList();

          return retrievalOutputPort
            .retrieve(icaos, concurrency)
            .map(result -> complete(findByIcao(savedRetrievals, result.icao()), result));
        }
      )
      .flatMap(retrievalPersistencePort::save, Math.max(1, concurrency));
  }

  @Override
  public Mono<Retrieval> retry(Retrieval retrieval) {
    if (!retrieval.isRetryable()) {
      return Mono.error(new IngestionException(INVALID_STATUS, retrieval.groupId() + " is not retryable."));
    }

    Retrieval retryRetrieval = retrieval.nextAttempt(Instant.now());

    return retrievalPersistencePort.save(retryRetrieval)
      .flatMap(savedRetrieval -> retrievalOutputPort
        .retry(savedRetrieval.icao())
        .map(result -> complete(savedRetrieval, result))
      )
      .flatMap(retrievalPersistencePort::save);
  }

  @Override
  public Flux<Retrieval> retry(List<Retrieval> retrievals, int concurrency) {
    List<Retrieval> notRetryable = retrievals.stream().filter(retrieval -> !retrieval.isRetryable()).toList();
    if (!notRetryable.isEmpty()) {
      String ids = notRetryable.stream()
        .map(Retrieval::groupId)
        .collect(Collectors.joining(", "));

      return Flux.error(new IngestionException(INVALID_STATUS, ids + " are not retryable."));
    }

    List<Retrieval> retryRetrievals = new HashSet<>(retrievals).stream()
      .map(retrieval -> retrieval.nextAttempt(Instant.now()))
      .toList();

    return Flux.fromIterable(retryRetrievals)
      .flatMap(retrievalPersistencePort::save, Math.max(1, concurrency))
      .collectList()
      .flatMapMany(savedRetrievals -> {
        List<String> icaos = savedRetrievals.stream().map(Retrieval::icao).toList();

        return retrievalOutputPort
          .retry(icaos, concurrency)
          .map(result -> complete(findByIcao(savedRetrievals, result.icao()), result));
      })
      .flatMap(retrievalPersistencePort::save, Math.max(1, concurrency));
  }

  private Retrieval createRetrieval(String icao) {
    String normalizeIcao = normalizeIcao(icao);
    Instant requestedAt = Instant.now();

    return Retrieval.create(newGroupId(), normalizeIcao, requestedAt);
  }

  private static String normalizeIcao(String icao) {
    if (icao == null || icao.isBlank()) return null;
    return icao.trim().toUpperCase(Locale.ROOT);
  }

  private String newGroupId() {
    return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
  }

  private static Retrieval complete(Retrieval retrieval, RetrievalResult result) {
    if (result.isSucceeded()) {
      retrieval.succeed(result.reportText(), Instant.now());
      return retrieval;
    }

    retrieval.fail(result.failureReason(), result.failureDetail(), Instant.now());
    return retrieval;
  }

  private static Retrieval findByIcao(List<Retrieval> retrievals, String icao) {
    return retrievals.stream()
      .filter(retrieval -> retrieval.icao().equalsIgnoreCase(icao))
      .findFirst()
      .orElseThrow(() -> new IngestionException(INVALID_STATUS, "Retrieval not found for ICAO: " + icao));
  }

}
