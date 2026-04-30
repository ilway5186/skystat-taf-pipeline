package com.skystat.taf.ingestion.storage.application;

import com.skystat.taf.ingestion.common.exception.IngestionErrorCode;
import com.skystat.taf.ingestion.common.exception.IngestionException;
import com.skystat.taf.ingestion.storage.application.port.TafPersistenceServicePort;
import com.skystat.taf.ingestion.storage.application.service.TafPersistenceService;
import com.skystat.taf.ingestion.storage.domain.vo.TafStorage;
import com.skystat.taf.ingestion.storage.domain.vo.TafParsingStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class TafPersistenceServiceTests {

  @Test
  void tafId로_TAF_저장여부를_알_수_있다() {
    TafPersistenceService fakeService = new TafPersistenceServiceImpl(new FakeTafPersistenceServicePort());
    String tafId1 = "abcd1234";
    String tafId2 = "no-exists";

    boolean find1 = fakeService.existsByTafId(tafId1);
    boolean find2 = fakeService.existsByTafId(tafId2);
    assertTrue(find1);
    assertFalse(find2);
  }

  @Test
  void 동일한_tafId로_StoredTaf를_insert하면_예외가_발생한다() {
    TafPersistenceService fakeService = new TafPersistenceServiceImpl(new FakeTafPersistenceServicePort());
    TafStorage alreadyExists = new TafStorage(
      "abcd1234",
      "awc",
      "rksi",
      "TAF RKSI 082300Z ...",
      Instant.parse("2026-04-23T07:00:00Z"),
      TafParsingStatus.SUCCEEDED,
      null
    );

    Assertions.assertThrows(IngestionException.class, () -> {
      fakeService.insert(alreadyExists);
    });
  }

  @Test
  void 중복되지_않는_tafId의_StoredTaf는_insert에_성공해야_한다() {
    TafPersistenceService fakeService = new TafPersistenceServiceImpl(new FakeTafPersistenceServicePort());
    TafStorage newOne = new TafStorage(
      "abcd12345",
      "awc",
      "rksi",
      "TAF RKSI 082300Z ...",
      Instant.parse("2026-04-23T07:00:00Z"),
      TafParsingStatus.SUCCEEDED,
      null
    );

    TafStorage inserted = fakeService.insert(newOne);
    assertEquals(newOne.tafId(), inserted.tafId());
    assertEquals(newOne.report(), inserted.report());
  }


  static class FakeTafPersistenceServicePort implements TafPersistenceServicePort {

    List<TafStorage> list = new ArrayList<>();

    public FakeTafPersistenceServicePort() {
      list.add(new TafStorage(
        "abcd1234",
        "awc",
        "rksi",
        "TAF RKSI 082300Z 0900/1006 09010KT 4000 -RA FEW010 BKN030 OVC070 TX12/0908Z TN09/0921Z TX12/1005Z",
        Instant.parse("2026-04-23T07:00:00Z"),
        TafParsingStatus.SUCCEEDED,
        null
      ));
    }

    @Override
    public boolean existsByTafId(String tafId) {
      return list.stream().anyMatch(taf -> taf.tafId().equals(tafId));
    }

    @Override
    public TafStorage insert(TafStorage tafStorage) {
      if (existsByTafId(tafStorage.tafId())) {
        throw new IngestionException(IngestionErrorCode.DUPLICATE_RESOURCE);
      }
      list.add(tafStorage);
      return tafStorage;
    }


    @Override
    public Optional<TafStorage> findByTafId(String tafId) {
      if (!existsByTafId(tafId)) return Optional.empty();

      return list.stream().filter(t -> t.tafId().equals(tafId)).findFirst();
    }

  }
}
