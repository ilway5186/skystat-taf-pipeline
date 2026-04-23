package com.skystat.taf.ingestion.storage.application.service;

import com.skystat.taf.ingestion.storage.domain.PublicationRequest;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface PublicationRequestPersistenceService {

  boolean existsByRequestId(String requestId);

  boolean existsAnyByRequestId(List<String> requestIds);

  boolean existsAllByRequestId(List<String> requestIds);

  PublicationRequest insert(PublicationRequest request);

  List<PublicationRequest> insertAll(List<PublicationRequest> requests);

  PublicationRequest markSent(String requestId, Instant publishedAt);

  PublicationRequest markFailed(String requestId, String failureReason, Instant failedAt);

  PublicationRequest retry(String requestId, Instant requestedAt);

  Optional<PublicationRequest> findByRequestId(String requestId);

}
