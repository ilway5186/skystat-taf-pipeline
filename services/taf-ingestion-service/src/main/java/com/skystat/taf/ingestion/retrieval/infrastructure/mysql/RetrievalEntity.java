package com.skystat.taf.ingestion.retrieval.infrastructure.mysql;

import com.skystat.taf.ingestion.retrieval.domain.Retrieval;
import com.skystat.taf.ingestion.retrieval.domain.RetrievalFailureReason;
import com.skystat.taf.ingestion.retrieval.domain.RetrievalStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Getter
@Accessors(fluent = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "retrieval")
public class RetrievalEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "group_id", nullable = false, updatable = false, length = 16)
  private String groupId;

  @Column(name = "icao", nullable = false, updatable = false, length = 4)
  private String icao;

  @Column(name = "requested_at", nullable = false)
  private Instant requestedAt;

  @Column(name = "retrieved_at")
  private Instant retrievedAt;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 32)
  private RetrievalStatus status;

  @Enumerated(EnumType.STRING)
  @Column(name = "failure_reason", length = 64)
  private RetrievalFailureReason failureReason;

  @Column(name = "failure_detail")
  private String failureDetail;

  @Column(name = "attempt_count", nullable = false)
  private int attemptCount;

  @CreatedDate
  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @LastModifiedDate
  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  public static RetrievalEntity from(Retrieval retrieval) {
    return from(retrieval, null);
  }

  public static RetrievalEntity from(Retrieval retrieval, Long id) {
    return new RetrievalEntity(
      id,
      retrieval.groupId(),
      retrieval.icao(),
      retrieval.requestedAt(),
      retrieval.retrievedAt(),
      retrieval.status(),
      retrieval.failureReason(),
      retrieval.failureDetail(),
      retrieval.attemptCount(),
      null,
      null
    );
  }

  public Retrieval toDomain() {
    return new Retrieval(
      groupId,
      icao,
      requestedAt,
      retrievedAt,
      status,
      failureReason,
      failureDetail,
      attemptCount
    );
  }
}
