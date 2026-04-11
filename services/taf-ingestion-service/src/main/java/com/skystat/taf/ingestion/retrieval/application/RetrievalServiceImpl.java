package com.skystat.taf.ingestion.retrieval.application;

import com.skystat.core.domain.service.parser.TafParser;
import com.skystat.taf.ingestion.common.exception.IngestionException;
import com.skystat.taf.ingestion.retrieval.domain.Retrieval;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
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

    return save(retrieval)
      .flatMap(savedRetrieval -> retrievalOutputPort
        .retrieve(savedRetrieval.icao())
        .map(result -> complete(retrieval, result))
      )
      .flatMap(this::save);
  }

  @Override
  public Flux<Retrieval> retrieve(List<String> icao, int concurrency) {
    List<Retrieval> retrievals = icao.stream().map(this::createRetrieval).toList();

    return Flux.fromIterable(retrievals)
      .flatMap(this::save, Math.max(1, concurrency))
      .collectList()
      .flatMapMany(savedRetrievals -> {
          List<String> icaos = savedRetrievals.stream().map(Retrieval::icao).toList();

          return retrievalOutputPort
            .retrieve(icaos, concurrency)
            .map(result -> complete(findByIcao(savedRetrievals, result.icao()), result));
        }
      )
      .flatMap(this::save, Math.max(1, concurrency));
  }

  @Override
  public Mono<Retrieval> retry(Retrieval retrieval) {
    if (!retrieval.retryable()) {
      return Mono.error(new IngestionException(INVALID_STATUS, retrieval.id() + " is not retryable."));
    }

    retrieval.requestRetry(Instant.now());

    return save(retrieval)
      .flatMap(savedRetrieval -> retrievalOutputPort
        .retry(savedRetrieval.icao())
        .map(result -> complete(savedRetrieval, result))
      )
      .flatMap(this::save);
  }

  @Override
  public Flux<Retrieval> retry(List<Retrieval> retrievals, int concurrency) {
    List<Retrieval> notRetryable = retrievals.stream().filter(retrieval -> !retrieval.retryable()).toList();
    if (!notRetryable.isEmpty()) {
      String ids = notRetryable.stream()
        .map(Retrieval::id)
        .collect(Collectors.joining(", "));

      return Flux.error(new IngestionException(INVALID_STATUS, ids + " are not retryable."));
    }

    retrievals.forEach(retrieval -> retrieval.requestRetry(Instant.now()));

    return Flux.fromIterable(retrievals)
      .flatMap(this::save, Math.max(1, concurrency))
      .collectList()
      .flatMapMany(savedRetrievals -> {
        List<String> icaos = savedRetrievals.stream().map(Retrieval::icao).toList();

        return retrievalOutputPort
          .retry(icaos, concurrency)
          .map(result -> complete(findByIcao(savedRetrievals, result.icao()), result));
      })
      .flatMap(this::save, Math.max(1, concurrency));
  }

  private static String normalizeIcao(String icao) {
    if (icao == null || icao.isBlank()) return null;
    return icao.trim().toUpperCase(Locale.ROOT);
  }

  private Retrieval createRetrieval(String icao) {
    String retrievalId = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    String normalizeIcao = normalizeIcao(icao);
    Instant requestedAt = Instant.now();

    return Retrieval.create(retrievalId, normalizeIcao, requestedAt);
  }

  private Mono<Retrieval> save(Retrieval retrieval) {
    return Mono.fromRunnable(() -> retrievalPersistencePort.save(retrieval))
      .subscribeOn(Schedulers.boundedElastic())
      .thenReturn(retrieval);
  }

  private static Retrieval complete(Retrieval retrieval, RetrievalResult result) {
    if (result.succeeded()) {
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
