package com.skystat.taf.ingestion.storage.infrastructure;

import com.skystat.taf.ingestion.storage.domain.vo.TafParsingStatus;
import com.skystat.taf.ingestion.storage.domain.vo.TafStorage;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

import static lombok.AccessLevel.*;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "taf_storage")
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
public class TafStorageEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "taf_id", nullable = false)
  private String tafId;

  @Column(name = "source_id", nullable = false)
  private String sourceId;

  @Column(name = "icao", nullable = false)
  private String icao;

  @Column(name = "report", nullable = false)
  private String report;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private TafParsingStatus status;

  @Column(name = "failure_reason")
  private String failureReason;

  @Column(name = "stored_at", nullable = false)
  private Instant storedAt;

  @CreatedDate
  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @LastModifiedDate
  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  public static TafStorageEntity from(TafStorage tafStorage) {
    return from(tafStorage, null);
  }

  public static TafStorageEntity from(TafStorage tafStorage, Long id) {
    return new TafStorageEntity(
        id,
        tafStorage.tafId(),
        tafStorage.sourceId(),
        tafStorage.icao(),
        tafStorage.report(),
        tafStorage.status(),
        tafStorage.failureReason(),
        tafStorage.storedAt(),
        null,
        null
    );
  }

  public TafStorage toDomain() {
    return new TafStorage(
      tafId,
      sourceId,
      icao,
      report,
      storedAt,
      status,
      failureReason
    );
  }

}
