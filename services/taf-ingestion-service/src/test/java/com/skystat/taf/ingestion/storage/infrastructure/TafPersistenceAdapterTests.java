package com.skystat.taf.ingestion.storage.infrastructure;

import com.skystat.taf.ingestion.TestcontainersConfiguration;
import com.skystat.taf.ingestion.common.config.JpaConfig;
import com.skystat.taf.ingestion.common.policy.IdGenerationPolicy;
import com.skystat.taf.ingestion.common.policy.UUIDGenerationPolicy;
import com.skystat.taf.ingestion.storage.application.port.TafPersistenceServicePort;
import com.skystat.taf.ingestion.storage.domain.vo.TafParsingStatus;
import com.skystat.taf.ingestion.storage.domain.vo.TafStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("sync")
@Import({
  JpaConfig.class,
  TafPersistenceAdapter.class,
  TestcontainersConfiguration.class,
  UUIDGenerationPolicy.class
})
public class TafPersistenceAdapterTests {

  @Autowired TafPersistenceServicePort adapter;
  @Autowired TafStorageJpaRepository repo;
  @Autowired IdGenerationPolicy idGenerator;

  @BeforeEach
  void setUp() {
    repo.deleteAll();
  }

  @Test
  void insert에_성공해야_한다() {
    String tafId = idGenerator.generate();
    Instant storedAt = Instant.now();
    TafStorage tafStorage = new TafStorage(tafId, "source1", "RKSI", "TAF RKSI ...", storedAt, TafParsingStatus.SUCCEEDED, null);

    TafStorage saved = adapter.insert(tafStorage);
    assertEquals(tafStorage.tafId(), saved.tafId());
    assertEquals(tafStorage.sourceId(), saved.sourceId());
  }

  @Test
  void tafId로_저장된_TAF의_존재유무를_확인할_수_있다() {
    String tafId = idGenerator.generate();
    String notExistingTafId = idGenerator.generate();
    Instant storedAt = Instant.now();
    TafStorage tafStorage = new TafStorage(tafId, "source1", "RKSI", "TAF RKSI ...", storedAt, TafParsingStatus.SUCCEEDED, null);

    adapter.insert(tafStorage);
    assertTrue(adapter.existsByTafId(tafId));
    assertFalse(adapter.existsByTafId(notExistingTafId));
  }

  @Test
  void tafId로_저장된_TAF를_존회할_수_있다() {
    String tafId = idGenerator.generate();
    Instant storedAt = Instant.now();
    TafStorage tafStorage = new TafStorage(tafId, "source1", "RKSI", "TAF RKSI ...", storedAt, TafParsingStatus.SUCCEEDED, null);

    adapter.insert(tafStorage);

    TafStorage find = adapter.findByTafId(tafId).get();
    assertEquals(tafStorage.tafId(), find.tafId());
  }



}
