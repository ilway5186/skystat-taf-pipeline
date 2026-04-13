package com.skystat.taf.ingestion.retrieval.infrastructure.reactive;

import com.skystat.taf.ingestion.TestcontainersConfiguration;
import com.skystat.taf.ingestion.retrieval.domain.Retrieval;
import com.skystat.taf.ingestion.retrieval.domain.RetrievalFailureReason;
import com.skystat.taf.ingestion.retrieval.domain.RetrievalStatus;
import com.skystat.taf.ingestion.retrieval.infrastructure.reactive.mysql.RetrievalPersistenceAdapter;
import com.skystat.taf.ingestion.retrieval.infrastructure.reactive.mysql.RetrievalR2dbcRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.r2dbc.test.autoconfigure.DataR2dbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataR2dbcTest
@ActiveProfiles("reactive")
@Import({
  RetrievalPersistenceAdapter.class,
  TestcontainersConfiguration.class
})
public class RetrievalPersistenceAdapterTests {

  @Autowired
  RetrievalPersistenceAdapter adapter;

  @Autowired
  RetrievalR2dbcRepository repo;


  @BeforeEach
  public void setup() {
    StepVerifier.create(repo.deleteAll())
      .verifyComplete();
  }

  @Test
  void contextLoads() {
  }

  @Test
  void save는_새로운_attempt를_insert해야한다() {
    Retrieval retrieval = Retrieval.create(newGroupId(), "rksi", Instant.now());
    StepVerifier.create(adapter.save(retrieval))
      .assertNext(savedRetrieval -> {
        assertEquals(retrieval.groupId(), savedRetrieval.groupId());
        assertEquals(retrieval.attemptCount(), savedRetrieval.attemptCount());
        assertEquals(retrieval.icao(), savedRetrieval.icao());
        assertEquals(RetrievalStatus.REQUESTED, savedRetrieval.status());
        assertEquals(1, savedRetrieval.attemptCount());
      })
      .verifyComplete();

    StepVerifier.create(repo.findAll().collectList())
      .assertNext(entities -> {
        assertEquals(1, entities.size());
        assertEquals(retrieval.groupId(), entities.getFirst().groupId());
        assertEquals(1, entities.getFirst().attemptCount());
      })
      .verifyComplete();
  }

  @Test
  void save는_같은_groupId와_attemptCount가_있으면_update해야한다() {
    Retrieval retrieval = Retrieval.create(newGroupId(), "rksi", Instant.now());

    // 요청을 날렸다고 기록 (status: REQUESTED)
    StepVerifier.create(adapter.save(retrieval))
      .expectNextCount(1)
      .verifyComplete();

    retrieval.succeed("TAF RKSI 130500Z ...", Instant.now());
    StepVerifier.create(adapter.save(retrieval))
      .assertNext(savedRetrieval -> {
        assertEquals(retrieval.groupId(), savedRetrieval.groupId());
        assertEquals(RetrievalStatus.SUCCEEDED, savedRetrieval.status());
        assertEquals("TAF RKSI 130500Z ...", savedRetrieval.reportText());
        assertEquals(1, savedRetrieval.attemptCount());
      })
      .verifyComplete();

    StepVerifier.create(repo.findAll().collectList())
      .assertNext(entities -> {
        assertEquals(1, entities.size());
        assertEquals("SUCCEEDED", entities.getFirst().status());
        assertEquals("TAF RKSI 130500Z ...", entities.getFirst().reportText());
      })
      .verifyComplete();
  }

  @Test
  void save는_다음_attempt를_별도_row로_insert해야한다() {
    Retrieval firstAttempt = Retrieval.create(newGroupId(), "rksi", Instant.now());
    firstAttempt.fail(RetrievalFailureReason.HTTP_SERVER_ERROR, "Provider returned 500.", Instant.now());

    Retrieval secondAttempt = firstAttempt.nextAttempt(Instant.now());
    secondAttempt.succeed("TAF RKSI 130500Z ...", Instant.now());

    StepVerifier.create(adapter.save(firstAttempt))
      .expectNextCount(1)
      .verifyComplete();
    StepVerifier.create(adapter.save(secondAttempt))
      .expectNextCount(1)
      .verifyComplete();

    StepVerifier.create(repo.findAllByGroupIdOrderByAttemptCountAsc(firstAttempt.groupId()).collectList())
      .assertNext(retrievalEntities -> {
        assertEquals(firstAttempt.groupId(), retrievalEntities.getFirst().groupId());
        assertEquals(1, retrievalEntities.getFirst().attemptCount());
        assertEquals("FAILED", retrievalEntities.getFirst().status());
        assertEquals("HTTP_SERVER_ERROR", retrievalEntities.getFirst().failureReason());

        assertEquals(firstAttempt.groupId(), retrievalEntities.get(1).groupId());
        assertEquals(2, retrievalEntities.get(1).attemptCount());
        assertEquals("SUCCEEDED", retrievalEntities.get(1).status());
      })
      .verifyComplete();
  }

  @Test
  void findAllByGroupId는_attemptCount_순서대로_히스토리를_조회해야한다() {
    Retrieval firstAttempt = Retrieval.create(newGroupId(), "RKSI", Instant.now());
    firstAttempt.fail(
      RetrievalFailureReason.HTTP_SERVER_ERROR,
      "Provider returned 500.",
      Instant.now()
    );

    Retrieval secondAttempt = firstAttempt.nextAttempt(Instant.now());
    secondAttempt.succeed("TAF RKSI 130600Z ...", Instant.now());

    // 순서대로 나오나 봐야되니까 일부러 뒤에것부터 저장
    StepVerifier.create(adapter.save(secondAttempt))
      .expectNextCount(1)
      .verifyComplete();
    StepVerifier.create(adapter.save(firstAttempt))
      .expectNextCount(1)
      .verifyComplete();

    StepVerifier.create(adapter.findAllByGroupId(firstAttempt.groupId()).collectList())
      .assertNext(retrievals -> {
        assertEquals(2, retrievals.size());

        assertEquals(1, retrievals.get(0).attemptCount());
        assertEquals(RetrievalStatus.FAILED, retrievals.get(0).status());

        assertEquals(2, retrievals.get(1).attemptCount());
        assertEquals(RetrievalStatus.SUCCEEDED, retrievals.get(1).status());
      })
      .verifyComplete();
  }

  private String newGroupId() {
    return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
  }

}
