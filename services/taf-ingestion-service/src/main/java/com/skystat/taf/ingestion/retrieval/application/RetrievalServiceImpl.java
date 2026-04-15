package com.skystat.taf.ingestion.retrieval.application;

import com.skystat.taf.ingestion.common.exception.IngestionException;
import com.skystat.taf.ingestion.retrieval.application.dto.RetrievalResult;
import com.skystat.taf.ingestion.retrieval.application.dto.RetrievalServiceResult;
import com.skystat.taf.ingestion.retrieval.application.service.RetrievalClientService;
import com.skystat.taf.ingestion.retrieval.application.service.RetrievalPersistenceService;
import com.skystat.taf.ingestion.retrieval.application.service.RetrievalService;
import com.skystat.taf.ingestion.retrieval.domain.Retrieval;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static com.skystat.taf.ingestion.common.exception.IngestionErrorCode.INVALID_STATUS;

@Service
@Profile("sync")
@RequiredArgsConstructor
public class RetrievalServiceImpl implements RetrievalService {

  private final RetrievalClientService retrievalClientService;
  private final RetrievalPersistenceService retrievalPersistenceService;

  @Override
  public RetrievalServiceResult retrieve(String icao) {
    Retrieval retrieval = createRetrieval(icao);
    Retrieval saved = retrievalPersistenceService.insert(retrieval);

    RetrievalResult result = retrievalClientService.retrieve(saved.icao());
    Retrieval completed = complete(saved, result);

    retrievalPersistenceService.update(completed);
    return RetrievalServiceResult.from(completed, result);
  }

  @Override
  public List<RetrievalServiceResult> retrieve(List<String> icao, int concurrency) {
    List<Retrieval> retrievals = new LinkedHashSet<>(icao).stream()
      .map(this::createRetrieval)
      .toList();

    List<Retrieval> saved = retrievalPersistenceService.insertAll(retrievals);
    List<String> icaos = saved.stream().map(Retrieval::icao).toList();

    Map<String, RetrievalResult> resultMapByIcao = retrievalClientService.retrieve(icaos, concurrency)
      .stream()
      .collect(Collectors.toMap(
        result -> result.icao().toUpperCase(Locale.ROOT),
        result -> result,
        (oldResult, newResult) -> oldResult
      ));

    List<Retrieval> completed = saved.stream()
      .map(savedRetrieval -> complete(savedRetrieval, resultFor(savedRetrieval, resultMapByIcao)))
      .toList();

    retrievalPersistenceService.updateAll(completed);
    return completed.stream()
      .map(retrieval -> RetrievalServiceResult.from(retrieval, resultFor(retrieval, resultMapByIcao)))
      .toList();
  }

  @Override
  public RetrievalServiceResult retry(Retrieval retrieval) {
    if (!retrieval.isRetryable()) {
      throw new IngestionException(INVALID_STATUS, retrieval.groupId() + " is not retryable.");
    }

    Retrieval retryRetrieval = retrieval.nextAttempt(Instant.now());
    Retrieval saved = retrievalPersistenceService.insert(retryRetrieval);

    RetrievalResult result = retrievalClientService.retrieve(saved.icao());
    Retrieval completed = complete(saved, result);

    retrievalPersistenceService.update(completed);
    return RetrievalServiceResult.from(completed, result);
  }

  @Override
  public List<RetrievalServiceResult> retry(List<Retrieval> retrievals, int concurrency) {
    List<Retrieval> notRetryable = retrievals.stream()
      .filter(retrieval -> !retrieval.isRetryable())
      .toList();

    if (!notRetryable.isEmpty()) {
      String ids = notRetryable.stream()
        .map(Retrieval::groupId)
        .collect(Collectors.joining(", "));

      throw new IngestionException(INVALID_STATUS, ids + " are not retryable.");
    }

    List<Retrieval> retryRetrievals = new LinkedHashSet<>(retrievals).stream()
      .map(retrieval -> retrieval.nextAttempt(Instant.now()))
      .toList();

    List<Retrieval> saved = retrievalPersistenceService.insertAll(retryRetrievals);
    List<String> icaos = saved.stream().map(Retrieval::icao).toList();

    Map<String, RetrievalResult> resultMapByIcao = retrievalClientService.retrieve(icaos, concurrency)
      .stream()
      .collect(Collectors.toMap(
        result -> result.icao().toUpperCase(Locale.ROOT),
        result -> result,
        (oldResult, newResult) -> oldResult
      ));

    List<Retrieval> completed = saved.stream()
      .map(savedRetrieval -> complete(savedRetrieval, resultFor(savedRetrieval, resultMapByIcao)))
      .toList();

    retrievalPersistenceService.updateAll(completed);
    return completed.stream()
      .map(retrieval -> RetrievalServiceResult.from(retrieval, resultFor(retrieval, resultMapByIcao)))
      .toList();
  }

  private Retrieval createRetrieval(String icao) {
    String normalizeIcao = normalizeIcao(icao);
    Instant requestedAt = Instant.now();

    return Retrieval.create(newGroupId(), normalizeIcao, requestedAt);
  }

  private String newGroupId() {
    return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
  }

  private String normalizeIcao(String icao) {
    if (icao == null || icao.isBlank()) return null;
    return icao.trim().toUpperCase(Locale.ROOT);
  }

  private Retrieval complete(Retrieval retrieval, RetrievalResult result) {
    if (result.isSucceeded()) {
      retrieval.succeed(Instant.now());
      return retrieval;
    }

    retrieval.fail(result.failureReason(), result.failureDetail(), Instant.now());
    return retrieval;
  }

  private RetrievalResult resultFor(Retrieval retrieval, Map<String, RetrievalResult> resultMapByIcao) {
    RetrievalResult result = resultMapByIcao.get(retrieval.icao());
    if (result == null) {
      throw new IngestionException(INVALID_STATUS, "Retrieval result not found for ICAO: " + retrieval.icao());
    }
    return result;
  }

}
