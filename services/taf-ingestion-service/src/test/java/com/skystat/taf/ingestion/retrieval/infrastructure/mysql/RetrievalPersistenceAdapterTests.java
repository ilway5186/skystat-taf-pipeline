package com.skystat.taf.ingestion.retrieval.infrastructure.mysql;

import com.skystat.taf.ingestion.TestcontainersConfiguration;
import com.skystat.taf.ingestion.common.config.JpaConfig;
import com.skystat.taf.ingestion.retrieval.domain.Retrieval;
import com.skystat.taf.ingestion.retrieval.domain.RetrievalFailureReason;
import com.skystat.taf.ingestion.retrieval.domain.RetrievalStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ActiveProfiles("sync")
@Import({
  JpaConfig.class,
  RetrievalPersistenceAdapter.class,
  RetrievalJdbcRepository.class,
  TestcontainersConfiguration.class
})
public class RetrievalPersistenceAdapterTests {

  @Autowired
  RetrievalPersistenceAdapter adapter;

  @Autowired
  RetrievalJpaRepository repo;

  @BeforeEach
  void setup() {
    repo.deleteAll();
  }

  @Test
  void insert는_새로운_attempt를_insert해야한다() {
    Retrieval retrieval = Retrieval.create(newGroupId(), "RKSI", Instant.now());

    Retrieval savedRetrieval = adapter.insert(retrieval);

    assertEquals(retrieval.groupId(), savedRetrieval.groupId());
    assertEquals(retrieval.attemptCount(), savedRetrieval.attemptCount());
    assertEquals(retrieval.icao(), savedRetrieval.icao());
    assertEquals(RetrievalStatus.REQUESTED, savedRetrieval.status());
    assertEquals(1, savedRetrieval.attemptCount());

    List<RetrievalEntity> entities = repo.findAll();
    assertEquals(1, entities.size());
    assertEquals(retrieval.groupId(), entities.getFirst().groupId());
    assertEquals(1, entities.getFirst().attemptCount());
  }

  @Test
  void update는_기존_attempt를_완료상태로_update해야한다() {
    Retrieval retrieval = Retrieval.create(newGroupId(), "RKSI", Instant.now());
    adapter.insert(retrieval);

    retrieval.succeed(Instant.now());
    Retrieval savedRetrieval = adapter.update(retrieval);

    assertEquals(retrieval.groupId(), savedRetrieval.groupId());
    assertEquals(RetrievalStatus.SUCCEEDED, savedRetrieval.status());
    assertEquals(1, savedRetrieval.attemptCount());

    List<RetrievalEntity> entities = repo.findAll();
    assertEquals(1, entities.size());
    assertEquals(RetrievalStatus.SUCCEEDED, entities.getFirst().status());
  }

  @Test
  void insert는_다음_attempt를_별도_row로_insert해야한다() {
    Retrieval firstAttempt = Retrieval.create(newGroupId(), "RKSI", Instant.now());
    firstAttempt.fail(RetrievalFailureReason.HTTP_SERVER_ERROR, "Provider returned 500.", Instant.now());

    Retrieval secondAttempt = firstAttempt.nextAttempt(Instant.now());
    secondAttempt.succeed(Instant.now());

    adapter.insert(firstAttempt);
    adapter.insert(secondAttempt);

    List<RetrievalEntity> entities = repo.findAllByGroupIdOrderByAttemptCountAsc(firstAttempt.groupId());
    assertEquals(2, entities.size());
    assertEquals(firstAttempt.groupId(), entities.getFirst().groupId());
    assertEquals(1, entities.getFirst().attemptCount());
    assertEquals(RetrievalStatus.FAILED, entities.getFirst().status());
    assertEquals(RetrievalFailureReason.HTTP_SERVER_ERROR, entities.getFirst().failureReason());
    assertEquals(2, entities.get(1).attemptCount());
    assertEquals(RetrievalStatus.SUCCEEDED, entities.get(1).status());
  }

  @Test
  void findAllByGroupId는_attemptCount_순서대로_히스토리를_조회해야한다() {
    Retrieval firstAttempt = Retrieval.create(newGroupId(), "RKSI", Instant.now());
    firstAttempt.fail(RetrievalFailureReason.HTTP_SERVER_ERROR, "Provider returned 500.", Instant.now());

    Retrieval secondAttempt = firstAttempt.nextAttempt(Instant.now());
    secondAttempt.succeed(Instant.now());

    adapter.insert(secondAttempt);
    adapter.insert(firstAttempt);

    List<Retrieval> retrievals = adapter.findAllByGroupId(firstAttempt.groupId());

    assertEquals(2, retrievals.size());
    assertEquals(1, retrievals.get(0).attemptCount());
    assertEquals(RetrievalStatus.FAILED, retrievals.get(0).status());
    assertEquals(2, retrievals.get(1).attemptCount());
    assertEquals(RetrievalStatus.SUCCEEDED, retrievals.get(1).status());
  }

  @Test
  void insertAll은_여러_attempt를_한번에_insert해야한다() {
    Retrieval rksiRetrieval = Retrieval.create(newGroupId(), "RKSI", Instant.now());
    Retrieval rkssRetrieval = Retrieval.create(newGroupId(), "RKSS", Instant.now());

    List<Retrieval> savedRetrievals = adapter.insertAll(List.of(rksiRetrieval, rkssRetrieval));

    assertEquals(2, savedRetrievals.size());
    assertEquals(2, repo.findAll().size());
  }

  @Test
  void updateAll은_여러_attempt를_한번에_update해야한다() {
    Retrieval rksiRetrieval = Retrieval.create(newGroupId(), "RKSI", Instant.now());
    Retrieval rkssRetrieval = Retrieval.create(newGroupId(), "RKSS", Instant.now());
    adapter.insertAll(List.of(rksiRetrieval, rkssRetrieval));

    rksiRetrieval.succeed(Instant.now());
    rkssRetrieval.fail(RetrievalFailureReason.EMPTY_RESPONSE, "AWC returned no TAF report.", Instant.now());
    List<Retrieval> savedRetrievals = adapter.updateAll(List.of(rksiRetrieval, rkssRetrieval));

    assertEquals(2, savedRetrievals.size());
    assertEquals(2, repo.findAll().size());
    assertEquals(1, savedRetrievals.stream().filter(Retrieval::isSucceeded).count());
    assertEquals(1, savedRetrievals.stream().filter(Retrieval::isFailed).count());
  }

  private String newGroupId() {
    return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
  }

}
