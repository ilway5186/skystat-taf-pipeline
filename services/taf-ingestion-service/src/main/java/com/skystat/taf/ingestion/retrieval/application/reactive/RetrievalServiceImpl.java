package com.skystat.taf.ingestion.retrieval.application.reactive;

import com.skystat.taf.ingestion.common.exception.IngestionException;
import com.skystat.taf.ingestion.retrieval.application.dto.RetrievalResult;
import com.skystat.taf.ingestion.retrieval.application.dto.RetrievalServiceResult;
import com.skystat.taf.ingestion.retrieval.domain.Retrieval;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static com.skystat.taf.ingestion.common.exception.IngestionErrorCode.INVALID_STATUS;

@Service
@Profile("reactive")
@RequiredArgsConstructor
public class RetrievalServiceImpl implements RetrievalService {

  private final RetrievalClientService retrievalClientService;
  private final RetrievalPersistencePort retrievalPersistencePort;

  @Override
  public Mono<RetrievalServiceResult> retrieve(String icao) {
    Retrieval retrieval = createRetrieval(icao);

    return retrievalPersistencePort.save(retrieval)
      .flatMap(savedRetrieval -> retrievalClientService
        .retrieve(savedRetrieval.icao())
        .flatMap(result -> retrievalPersistencePort
          .save(complete(savedRetrieval, result))
          .map(completed -> RetrievalServiceResult.from(completed, result))
        ));
  }

  @Override
  public Flux<RetrievalServiceResult> retrieve(List<String> icao, int concurrency) {
    List<Retrieval> retrievals = new HashSet<>(icao).stream()
      .map(this::createRetrieval).toList();

    return Flux.fromIterable(retrievals)
      .flatMap(retrievalPersistencePort::save, Math.max(1, concurrency))
      .collectList()
      .flatMapMany(savedRetrievals -> {
          List<String> icaos = savedRetrievals.stream().map(Retrieval::icao).toList();

          return retrievalClientService
            .retrieve(icaos, concurrency)
            .flatMap(result -> saveCompleted(savedRetrievals, result), Math.max(1, concurrency));
        }
      );
  }

  @Override
  public Mono<RetrievalServiceResult> retry(Retrieval retrieval) {
    if (!retrieval.isRetryable()) {
      return Mono.error(new IngestionException(INVALID_STATUS, retrieval.groupId() + " is not retryable."));
    }

    Retrieval retryRetrieval = retrieval.nextAttempt(Instant.now());

    return retrievalPersistencePort.save(retryRetrieval)
      .flatMap(savedRetrieval -> retrievalClientService
        .retrieve(savedRetrieval.icao())
        .flatMap(result -> retrievalPersistencePort
          .save(complete(savedRetrieval, result))
          .map(completed -> RetrievalServiceResult.from(completed, result))
        ));
  }

  @Override
  public Flux<RetrievalServiceResult> retry(List<Retrieval> retrievals, int concurrency) {
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

        return retrievalClientService
          .retrieve(icaos, concurrency)
          .flatMap(result -> saveCompleted(savedRetrievals, result), Math.max(1, concurrency));
      });
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
      retrieval.succeed(Instant.now());
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

  private Mono<RetrievalServiceResult> saveCompleted(List<Retrieval> savedRetrievals, RetrievalResult result) {
    Retrieval savedRetrieval = findByIcao(savedRetrievals, result.icao());

    return retrievalPersistencePort
      .save(complete(savedRetrieval, result))
      .map(completed -> RetrievalServiceResult.from(completed, result));
  }

}
