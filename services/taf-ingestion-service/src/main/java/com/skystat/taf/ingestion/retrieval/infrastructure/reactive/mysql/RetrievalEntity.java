package com.skystat.taf.ingestion.retrieval.infrastructure.reactive.mysql;

import com.skystat.taf.ingestion.retrieval.domain.Retrieval;
import com.skystat.taf.ingestion.retrieval.domain.RetrievalFailureReason;
import com.skystat.taf.ingestion.retrieval.domain.RetrievalStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Getter
@Accessors(fluent = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table("retrievals")
public class RetrievalEntity {

  @Id
  @Column("id")
  private Long id;

  @Column("group_id")
  private String groupId;

  @Column("icao")
  private String icao;

  @Column("requested_at")
  private Instant requestedAt;

  @Column("retrieved_at")
  private Instant retrievedAt;

  @Column("status")
  private String status;

  @Column("failure_reason")
  private String failureReason;

  @Column("failure_detail")
  private String failureDetail;

  @Column("attempt_count")
  private int attemptCount;

  @ReadOnlyProperty
  @Column("created_at")
  private Instant createdAt;

  @ReadOnlyProperty
  @Column("updated_at")
  private Instant updatedAt;

  public static RetrievalEntity from(Retrieval retrieval, Long id) {
    return new RetrievalEntity(
      id,
      retrieval.groupId(),
      retrieval.icao(),
      retrieval.requestedAt(),
      retrieval.retrievedAt(),
      retrieval.status().name(),
      enumName(retrieval.failureReason()),
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
      RetrievalStatus.valueOf(status),
      enumValue(failureReason, RetrievalFailureReason.class),
      failureDetail,
      attemptCount
    );
  }

  private static String enumName(Enum<?> value) {
    return value == null ? null : value.name();
  }

  private static <E extends Enum<E>> E enumValue(String value, Class<E> type) {
    return value == null ? null : Enum.valueOf(type, value);
  }

}
