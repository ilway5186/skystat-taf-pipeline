package com.skystat.taf.ingestion.storage.application;

import com.skystat.taf.ingestion.common.exception.IngestionErrorCode;
import com.skystat.taf.ingestion.common.exception.IngestionException;
import com.skystat.taf.ingestion.storage.application.service.TafPersistenceService;
import com.skystat.taf.ingestion.storage.domain.vo.StoredTaf;
import com.skystat.taf.ingestion.storage.domain.vo.TafParsingStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TafPersistenceServiceTests {

  Instant time = Instant.now();

  @Test
  void tafId로_TAF_저장여부를_알_수_있다() {
    TafPersistenceService fakeService = new FakeTafPersistenceService();
    String tafId1 = "abcd1234";
    String tafId2 = "no-exists";

    boolean find1 = fakeService.existsByTafId(tafId1);
    boolean find2 = fakeService.existsByTafId(tafId2);
    assertTrue(find1);
    assertFalse(find2);
  }

  @Test
  void 동일한_tafId로_StoredTaf를_insert하면_예외가_발생한다() {
    TafPersistenceService fakeService = new FakeTafPersistenceService();
    StoredTaf alreadyExists = new StoredTaf(
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
    TafPersistenceService fakeService = new FakeTafPersistenceService();
    StoredTaf newOne = new StoredTaf(
      "abcd12345",
      "awc",
      "rksi",
      "TAF RKSI 082300Z ...",
      Instant.parse("2026-04-23T07:00:00Z"),
      TafParsingStatus.SUCCEEDED,
      null
    );

    StoredTaf inserted = fakeService.insert(newOne);
    assertEquals(newOne.tafId(), inserted.tafId());
    assertEquals(newOne.report(), inserted.report());
  }


  static class FakeTafPersistenceService implements TafPersistenceService {

    List<StoredTaf> list = new ArrayList<>();

    public FakeTafPersistenceService() {
      list.add(new StoredTaf(
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
    public StoredTaf insert(StoredTaf storedTaf) {
      if (existsByTafId(storedTaf.tafId())) {
        throw new IngestionException(IngestionErrorCode.DUPLICATE_RESOURCE);
      }
      list.add(storedTaf);
      return storedTaf;
    }
  }

}
