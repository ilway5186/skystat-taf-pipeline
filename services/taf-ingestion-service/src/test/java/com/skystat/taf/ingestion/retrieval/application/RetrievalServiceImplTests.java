package com.skystat.taf.ingestion.retrieval.application;

import com.skystat.taf.ingestion.common.exception.IngestionException;
import com.skystat.taf.ingestion.retrieval.application.dto.RetrievalResult;
import com.skystat.taf.ingestion.retrieval.application.dto.RetrievalServiceResult;
import com.skystat.taf.ingestion.retrieval.application.service.RetrievalClientService;
import com.skystat.taf.ingestion.retrieval.application.service.RetrievalPersistenceService;
import com.skystat.taf.ingestion.retrieval.domain.Retrieval;
import com.skystat.taf.ingestion.retrieval.domain.RetrievalFailureReason;
import com.skystat.taf.ingestion.retrieval.domain.RetrievalStatus;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RetrievalServiceImplTests {

  @Test
  void retrieve는_요청을_저장하고_외부조회_성공결과를_완료상태로_저장해야한다() {
    FakeRetrievalClientService clientService = new FakeRetrievalClientService();
    FakeRetrievalPersistenceService persistenceService = new FakeRetrievalPersistenceService();
    RetrievalServiceImpl service = new RetrievalServiceImpl(clientService, persistenceService);
    clientService.retrieveResult = RetrievalResult.success("RKSI", "TAF RKSI 130500Z ...");

    RetrievalServiceResult result = service.retrieve("rksi");

    assertNotNull(result.groupId());
    assertEquals("RKSI", result.icao());
    assertEquals("TAF RKSI 130500Z ...", result.reportText());
    assertTrue(result.isSucceeded());

    assertEquals(List.of("RKSI"), clientService.retrieveIcaos);
    assertEquals(2, persistenceService.savedRetrievals.size());
    assertEquals(RetrievalStatus.REQUESTED, persistenceService.savedRetrievals.get(0).status());
    assertEquals(RetrievalStatus.SUCCEEDED, persistenceService.savedRetrievals.get(1).status());
    assertEquals(1, persistenceService.savedRetrievals.get(1).attemptCount());
    assertEquals(result.groupId(), persistenceService.savedRetrievals.get(1).groupId());
    assertEquals(persistenceService.savedRetrievals.get(0).groupId(), persistenceService.savedRetrievals.get(1).groupId());
  }

  @Test
  void retrieve는_외부조회_실패결과를_실패상태로_저장해야한다() {
    FakeRetrievalClientService clientService = new FakeRetrievalClientService();
    FakeRetrievalPersistenceService persistenceService = new FakeRetrievalPersistenceService();
    RetrievalServiceImpl service = new RetrievalServiceImpl(clientService, persistenceService);
    clientService.retrieveResult = RetrievalResult.failure(
      "RKSI",
      RetrievalFailureReason.HTTP_SERVER_ERROR,
      "Provider returned 500."
    );

    RetrievalServiceResult result = service.retrieve("rksi");

    assertNotNull(result.groupId());
    assertEquals("RKSI", result.icao());
    assertTrue(result.isFailed());
    assertEquals(RetrievalFailureReason.HTTP_SERVER_ERROR, result.failureReason());
    assertEquals("Provider returned 500.", result.failureDetail());

    assertEquals(2, persistenceService.savedRetrievals.size());
    assertEquals(RetrievalStatus.REQUESTED, persistenceService.savedRetrievals.get(0).status());
    assertEquals(RetrievalStatus.FAILED, persistenceService.savedRetrievals.get(1).status());
    assertEquals(result.groupId(), persistenceService.savedRetrievals.get(1).groupId());
    assertEquals(1, persistenceService.savedRetrievals.get(1).attemptCount());
  }

  @Test
  void retrieve는_여러_공항을_저장하고_외부조회_성공결과를_완료상태로_저장해야한다() {
    FakeRetrievalClientService clientService = new FakeRetrievalClientService();
    FakeRetrievalPersistenceService persistenceService = new FakeRetrievalPersistenceService();
    RetrievalServiceImpl service = new RetrievalServiceImpl(clientService, persistenceService);

    List<RetrievalServiceResult> results = service.retrieve(List.of("rksi", "rkss"), 2);

    assertEquals(2, results.size());
    assertEquals(2, results.stream().filter(RetrievalServiceResult::isSucceeded).count());
    assertEquals(2, results.stream().filter(result -> result.groupId() != null).count());

    assertEquals(List.of("RKSI", "RKSS"), clientService.retrieveIcaos);
    assertEquals(4, persistenceService.savedRetrievals.size());
    assertEquals(2, persistenceService.savedRetrievals.stream()
      .filter(retrieval -> retrieval.status() == RetrievalStatus.REQUESTED)
      .count());
    assertEquals(2, persistenceService.savedRetrievals.stream()
      .filter(retrieval -> retrieval.status() == RetrievalStatus.SUCCEEDED)
      .count());
    assertEquals(4, persistenceService.savedRetrievals.stream()
      .filter(retrieval -> retrieval.attemptCount() == 1)
      .count());
  }

  @Test
  void retry는_같은_groupId로_다음_attempt를_저장하고_재조회해야한다() {
    FakeRetrievalClientService clientService = new FakeRetrievalClientService();
    FakeRetrievalPersistenceService persistenceService = new FakeRetrievalPersistenceService();
    RetrievalServiceImpl service = new RetrievalServiceImpl(clientService, persistenceService);
    Retrieval failedRetrieval = failedRetryableRetrieval();
    clientService.retrieveResult = RetrievalResult.success("RKSI", "TAF RKSI 130600Z ...");

    RetrievalServiceResult result = service.retry(failedRetrieval);

    assertEquals(failedRetrieval.groupId(), result.groupId());
    assertEquals("RKSI", result.icao());
    assertEquals("TAF RKSI 130600Z ...", result.reportText());
    assertTrue(result.isSucceeded());

    assertEquals(List.of("RKSI"), clientService.retrieveIcaos);
    assertEquals(2, persistenceService.savedRetrievals.size());
    assertEquals(RetrievalStatus.REQUESTED, persistenceService.savedRetrievals.get(0).status());
    assertEquals(RetrievalStatus.SUCCEEDED, persistenceService.savedRetrievals.get(1).status());
    assertEquals(failedRetrieval.groupId(), persistenceService.savedRetrievals.get(0).groupId());
    assertEquals(2, persistenceService.savedRetrievals.get(0).attemptCount());
    assertEquals(2, persistenceService.savedRetrievals.get(1).attemptCount());
  }

  @Test
  void retry는_여러_수집건의_다음_attempt를_저장하고_재조회해야한다() {
    FakeRetrievalClientService clientService = new FakeRetrievalClientService();
    FakeRetrievalPersistenceService persistenceService = new FakeRetrievalPersistenceService();
    RetrievalServiceImpl service = new RetrievalServiceImpl(clientService, persistenceService);
    Retrieval rksiRetrieval = failedRetryableRetrieval("RKSI");
    Retrieval rkssRetrieval = failedRetryableRetrieval("RKSS");

    List<RetrievalServiceResult> results = service.retry(List.of(rksiRetrieval, rkssRetrieval), 2);

    assertEquals(2, results.size());
    assertEquals(2, results.stream().filter(RetrievalServiceResult::isSucceeded).count());
    assertEquals(2, results.stream().filter(result -> result.groupId() != null).count());

    assertEquals(List.of("RKSI", "RKSS"), clientService.retrieveIcaos);
    assertEquals(4, persistenceService.savedRetrievals.size());
    assertEquals(2, persistenceService.savedRetrievals.stream()
      .filter(retrieval -> retrieval.status() == RetrievalStatus.REQUESTED)
      .count());
    assertEquals(2, persistenceService.savedRetrievals.stream()
      .filter(retrieval -> retrieval.status() == RetrievalStatus.SUCCEEDED)
      .count());
    assertEquals(4, persistenceService.savedRetrievals.stream()
      .filter(retrieval -> retrieval.attemptCount() == 2)
      .count());
  }

  @Test
  void retry는_retryable_상태가_아니면_저장과_외부조회를_수행하지_않아야한다() {
    FakeRetrievalClientService clientService = new FakeRetrievalClientService();
    FakeRetrievalPersistenceService persistenceService = new FakeRetrievalPersistenceService();
    RetrievalServiceImpl service = new RetrievalServiceImpl(clientService, persistenceService);
    Retrieval retrieval = Retrieval.create(newGroupId(), "RKSI", Instant.now());
    retrieval.fail(RetrievalFailureReason.HTTP_CLIENT_ERROR, "Bad request.", Instant.now());

    assertThrows(IngestionException.class, () -> service.retry(retrieval));

    assertEquals(0, clientService.retrieveIcaos.size());
    assertEquals(0, persistenceService.savedRetrievals.size());
  }

  private static Retrieval failedRetryableRetrieval() {
    return failedRetryableRetrieval("RKSI");
  }

  private static Retrieval failedRetryableRetrieval(String icao) {
    Retrieval retrieval = Retrieval.create(newGroupId(), icao, Instant.now());
    retrieval.fail(RetrievalFailureReason.HTTP_SERVER_ERROR, "Provider returned 500.", Instant.now());
    return retrieval;
  }

  private static String newGroupId() {
    return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
  }

  private static Retrieval copyOf(Retrieval retrieval) {
    return new Retrieval(
      retrieval.groupId(),
      retrieval.icao(),
      retrieval.requestedAt(),
      retrieval.retrievedAt(),
      retrieval.status(),
      retrieval.failureReason(),
      retrieval.failureDetail(),
      retrieval.attemptCount()
    );
  }

  private static class FakeRetrievalClientService implements RetrievalClientService {

    private RetrievalResult retrieveResult;
    private final List<String> retrieveIcaos = new ArrayList<>();

    @Override
    public RetrievalResult retrieve(String icao) {
      retrieveIcaos.add(icao);
      return retrieveResult;
    }

    @Override
    public List<RetrievalResult> retrieve(List<String> icaos, int concurrency) {
      retrieveIcaos.addAll(icaos);
      return icaos.stream()
        .map(icao -> RetrievalResult.success(icao, "TAF " + icao))
        .toList();
    }

  }

  private static class FakeRetrievalPersistenceService implements RetrievalPersistenceService {

    private final List<Retrieval> savedRetrievals = new ArrayList<>();

    @Override
    public boolean existsByGroupIdAndAttemptCount(String groupId, int attemptCount) {
      return false;
    }

    @Override
    public Retrieval insert(Retrieval retrieval) {
      Retrieval savedRetrieval = copyOf(retrieval);
      savedRetrievals.add(savedRetrieval);
      return copyOf(savedRetrieval);
    }

    @Override
    public Retrieval update(Retrieval retrieval) {
      Retrieval savedRetrieval = copyOf(retrieval);
      savedRetrievals.add(savedRetrieval);
      return copyOf(savedRetrieval);
    }

    @Override
    public List<Retrieval> insertAll(List<Retrieval> retrievals) {
      return saveAll(retrievals);
    }

    @Override
    public List<Retrieval> updateAll(List<Retrieval> retrievals) {
      return saveAll(retrievals);
    }

    @Override
    public Optional<Retrieval> findByGroupIdAndAttemptCount(String groupId, int attemptCount) {
      return Optional.empty();
    }

    @Override
    public List<Retrieval> findAllByGroupId(String groupId) {
      return List.of();
    }

    private List<Retrieval> saveAll(List<Retrieval> retrievals) {
      List<Retrieval> saved = retrievals.stream()
        .map(RetrievalServiceImplTests::copyOf)
        .toList();
      savedRetrievals.addAll(saved);
      return saved.stream()
        .map(RetrievalServiceImplTests::copyOf)
        .toList();
    }

  }

}
