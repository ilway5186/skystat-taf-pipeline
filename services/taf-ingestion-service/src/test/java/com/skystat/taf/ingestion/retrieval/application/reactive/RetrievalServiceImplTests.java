package com.skystat.taf.ingestion.retrieval.application.reactive;

import com.skystat.taf.ingestion.common.exception.IngestionException;
import com.skystat.taf.ingestion.retrieval.application.dto.RetrievalResult;
import com.skystat.taf.ingestion.retrieval.domain.Retrieval;
import com.skystat.taf.ingestion.retrieval.domain.RetrievalFailureReason;
import com.skystat.taf.ingestion.retrieval.domain.RetrievalStatus;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class RetrievalServiceImplTests {

  @Test
  void retrieve는_요청을_저장하고_외부조회_성공결과를_완료상태로_저장해야한다() {
    FakeRetrievalOutputPort outputPort = new FakeRetrievalOutputPort();
    FakeRetrievalPersistencePort persistencePort = new FakeRetrievalPersistencePort();
    RetrievalServiceImpl service = new RetrievalServiceImpl(outputPort, persistencePort);
    outputPort.retrieveResult = RetrievalResult.success("RKSI", "TAF RKSI 130500Z ...");

    StepVerifier.create(service.retrieve("rksi"))
      .assertNext(retrieval -> {
        assertNotNull(retrieval.groupId());
        assertEquals("RKSI", retrieval.icao());
        assertEquals(RetrievalStatus.SUCCEEDED, retrieval.status());
        assertEquals("TAF RKSI 130500Z ...", retrieval.reportText());
        assertEquals(1, retrieval.attemptCount());
      })
      .verifyComplete();

    assertEquals(List.of("RKSI"), outputPort.retrieveIcaos);
    assertEquals(2, persistencePort.savedRetrievals.size()); // REQUESTED 1개, SUCCESS 1개
    assertEquals(RetrievalStatus.REQUESTED, persistencePort.savedRetrievals.get(0).status());
    assertEquals(RetrievalStatus.SUCCEEDED, persistencePort.savedRetrievals.get(1).status());
    assertEquals(persistencePort.savedRetrievals.get(0).groupId(), persistencePort.savedRetrievals.get(1).groupId());
  }

  @Test
  void retrieve는_외부조회_실패결과를_실패상태로_저장해야한다() {
    FakeRetrievalOutputPort outputPort = new FakeRetrievalOutputPort();
    FakeRetrievalPersistencePort persistencePort = new FakeRetrievalPersistencePort();
    RetrievalServiceImpl service = new RetrievalServiceImpl(outputPort, persistencePort);
    outputPort.retrieveResult = RetrievalResult.failure(
      "RKSI",
      RetrievalFailureReason.HTTP_SERVER_ERROR,
      "Provider returned 500."
    );

    StepVerifier.create(service.retrieve("rksi"))
      .assertNext(retrieval -> {
        assertEquals("RKSI", retrieval.icao());
        assertEquals(RetrievalStatus.FAILED, retrieval.status());
        assertEquals(RetrievalFailureReason.HTTP_SERVER_ERROR, retrieval.failureReason());
        assertEquals("Provider returned 500.", retrieval.failureDetail());
        assertEquals(1, retrieval.attemptCount());
      })
      .verifyComplete();

    assertEquals(2, persistencePort.savedRetrievals.size());
    assertEquals(RetrievalStatus.REQUESTED, persistencePort.savedRetrievals.get(0).status());
    assertEquals(RetrievalStatus.FAILED, persistencePort.savedRetrievals.get(1).status());
  }

  @Test
  void retrieve는_여러_공항을_저장하고_외부조회_성공결과를_완료상태로_저장해야한다() {
    FakeRetrievalOutputPort outputPort = new FakeRetrievalOutputPort();
    FakeRetrievalPersistencePort persistencePort = new FakeRetrievalPersistencePort();
    RetrievalServiceImpl service = new RetrievalServiceImpl(outputPort, persistencePort);

    StepVerifier.create(service.retrieve(List.of("rksi", "rkss"), 2).collectList())
      .assertNext(retrievals -> {
        assertEquals(2, retrievals.size());
        assertEquals(2, retrievals.stream().filter(Retrieval::isSucceeded).count());
        assertEquals(2, retrievals.stream().filter(retrieval -> retrieval.attemptCount() == 1).count());
      })
      .verifyComplete();

    assertEquals(2, outputPort.retrieveIcaos.size());
    assertEquals(4, persistencePort.savedRetrievals.size());
    assertEquals(2, persistencePort.savedRetrievals.stream()
      .filter(retrieval -> retrieval.status() == RetrievalStatus.REQUESTED)
      .count());
    assertEquals(2, persistencePort.savedRetrievals.stream()
      .filter(retrieval -> retrieval.status() == RetrievalStatus.SUCCEEDED)
      .count());
  }

  @Test
  void retry는_같은_groupId로_다음_attempt를_저장하고_재조회해야한다() {
    FakeRetrievalOutputPort outputPort = new FakeRetrievalOutputPort();
    FakeRetrievalPersistencePort persistencePort = new FakeRetrievalPersistencePort();
    RetrievalServiceImpl service = new RetrievalServiceImpl(outputPort, persistencePort);
    Retrieval failedRetrieval = failedRetryableRetrieval();
    outputPort.retryResult = RetrievalResult.success("RKSI", "TAF RKSI 130600Z ...");

    StepVerifier.create(service.retry(failedRetrieval))
      .assertNext(retrieval -> {
        assertEquals(failedRetrieval.groupId(), retrieval.groupId());
        assertEquals("RKSI", retrieval.icao());
        assertEquals(RetrievalStatus.SUCCEEDED, retrieval.status());
        assertEquals("TAF RKSI 130600Z ...", retrieval.reportText());
        assertEquals(2, retrieval.attemptCount());
      })
      .verifyComplete();

    assertEquals(List.of("RKSI"), outputPort.retryIcaos);
    assertEquals(2, persistencePort.savedRetrievals.size());
    assertEquals(RetrievalStatus.REQUESTED, persistencePort.savedRetrievals.get(0).status());
    assertEquals(RetrievalStatus.SUCCEEDED, persistencePort.savedRetrievals.get(1).status());
    assertEquals(failedRetrieval.groupId(), persistencePort.savedRetrievals.get(0).groupId());
    assertEquals(2, persistencePort.savedRetrievals.get(0).attemptCount());
  }

  @Test
  void retry는_여러_수집건의_다음_attempt를_저장하고_재조회해야한다() {
    FakeRetrievalOutputPort outputPort = new FakeRetrievalOutputPort();
    FakeRetrievalPersistencePort persistencePort = new FakeRetrievalPersistencePort();
    RetrievalServiceImpl service = new RetrievalServiceImpl(outputPort, persistencePort);
    Retrieval rksiRetrieval = failedRetryableRetrieval("RKSI");
    Retrieval rkssRetrieval = failedRetryableRetrieval("RKSS");

    StepVerifier.create(service.retry(List.of(rksiRetrieval, rkssRetrieval), 2).collectList())
      .assertNext(retrievals -> {
        assertEquals(2, retrievals.size());
        assertEquals(2, retrievals.stream().filter(Retrieval::isSucceeded).count());
        assertEquals(2, retrievals.stream().filter(retrieval -> retrieval.attemptCount() == 2).count());
      })
      .verifyComplete();

    assertEquals(2, outputPort.retryIcaos.size());
    assertEquals(4, persistencePort.savedRetrievals.size());
    assertEquals(2, persistencePort.savedRetrievals.stream()
      .filter(retrieval -> retrieval.status() == RetrievalStatus.REQUESTED)
      .count());
    assertEquals(2, persistencePort.savedRetrievals.stream()
      .filter(retrieval -> retrieval.status() == RetrievalStatus.SUCCEEDED)
      .count());
  }

  @Test
  void retry는_retryable_상태가_아니면_저장과_외부조회를_수행하지_않아야한다() {
    FakeRetrievalOutputPort outputPort = new FakeRetrievalOutputPort();
    FakeRetrievalPersistencePort persistencePort = new FakeRetrievalPersistencePort();
    RetrievalServiceImpl service = new RetrievalServiceImpl(outputPort, persistencePort);
    Retrieval retrieval = Retrieval.create(newGroupId(), "RKSI", Instant.now());

    retrieval.fail(RetrievalFailureReason.HTTP_CLIENT_ERROR, "Bad request.", Instant.now());

    StepVerifier.create(service.retry(retrieval))
      .expectError(IngestionException.class)
      .verify();

    assertEquals(0, outputPort.retryIcaos.size());
    assertEquals(0, persistencePort.savedRetrievals.size());
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
      retrieval.reportText(),
      retrieval.requestedAt(),
      retrieval.retrievedAt(),
      retrieval.status(),
      retrieval.failureReason(),
      retrieval.failureDetail(),
      retrieval.attemptCount()
    );
  }

  private static class FakeRetrievalOutputPort implements RetrievalOutputPort {

    private RetrievalResult retrieveResult;
    private RetrievalResult retryResult;
    private final List<String> retrieveIcaos = new ArrayList<>();
    private final List<String> retryIcaos = new ArrayList<>();

    @Override
    public Mono<RetrievalResult> retrieve(String icao) {
      retrieveIcaos.add(icao);
      return Mono.just(retrieveResult);
    }

    @Override
    public Flux<RetrievalResult> retrieve(List<String> icaos, int concurrency) {
      retrieveIcaos.addAll(icaos);
      return Flux.fromIterable(icaos).map(icao -> RetrievalResult.success(icao, "TAF " + icao));
    }

    @Override
    public Mono<RetrievalResult> retry(String icao) {
      retryIcaos.add(icao);
      return Mono.just(retryResult);
    }

    @Override
    public Flux<RetrievalResult> retry(List<String> icaos, int concurrency) {
      retryIcaos.addAll(icaos);
      return Flux.fromIterable(icaos).map(icao -> RetrievalResult.success(icao, "TAF " + icao));
    }

  }

  private static class FakeRetrievalPersistencePort implements RetrievalPersistencePort {

    private final List<Retrieval> savedRetrievals = new ArrayList<>();

    @Override
    public Mono<Retrieval> save(Retrieval retrieval) {
      Retrieval savedRetrieval = copyOf(retrieval);
      savedRetrievals.add(savedRetrieval);
      return Mono.just(copyOf(savedRetrieval));
    }

    @Override
    public Flux<Retrieval> findAllByGroupId(String groupId) {
      return Flux.empty();
    }

  }

}
