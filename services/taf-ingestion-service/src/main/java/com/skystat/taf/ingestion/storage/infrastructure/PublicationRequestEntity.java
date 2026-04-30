package com.skystat.taf.ingestion.storage.infrastructure;

import com.skystat.taf.ingestion.retrieval.domain.Retrieval;
import com.skystat.taf.ingestion.retrieval.infrastructure.mysql.RetrievalEntity;
import com.skystat.taf.ingestion.storage.domain.PublicationRequest;
import com.skystat.taf.ingestion.storage.domain.PublicationRequestStatus;
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
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "publication_request")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PublicationRequestEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "request_id", nullable = false)
  private String requestId;

  @Column(name = "taf_id", nullable = false)
  private String tafId;

  @Column(name = "icao", nullable = false)
  private String icao;

  @Column(name = "status", nullable = false)
  private PublicationRequestStatus status;

  @Column(name = "requested_at", nullable = false)
  private Instant requestedAt;

  @Column(name = "published_at")
  private Instant publishedAt;

  @Column(name = "failed_at")
  private Instant failedAt;

  @Column(name = "failure_reason")
  private String failureReason;

  @Column(name = "retry_count")
  private int retryCount;

  @CreatedDate
  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @LastModifiedDate
  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  public static PublicationRequestEntity from(PublicationRequest request) {
    return from(request, null);
  }

  public static PublicationRequestEntity from(PublicationRequest request, Long id) {
    return new PublicationRequestEntity(
      id,
      request.requestId(),
      request.tafId(),
      request.icao(),
      request.status(),
      request.requestedAt(),
      request.publishedAt(),
      request.failedAt(),
      request.failureReason(),
      request.retryCount(),
      null,
      null
    );
  }

  public PublicationRequest toDomain() {
    return new PublicationRequest(
      requestId,
      tafId,
      icao,
      status,
      requestedAt,
      publishedAt,
      failedAt,
      failureReason,
      retryCount
    );
  }

}
