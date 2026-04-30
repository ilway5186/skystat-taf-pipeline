package com.skystat.taf.ingestion.storage.application;

import com.skystat.taf.ingestion.common.exception.IngestionErrorCode;
import com.skystat.taf.ingestion.common.exception.IngestionException;
import com.skystat.taf.ingestion.storage.application.port.PublicationRequestPersistencePort;
import com.skystat.taf.ingestion.storage.application.service.PublicationRequestPersistenceService;
import com.skystat.taf.ingestion.storage.domain.PublicationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PublicationRequestPersistenceServiceImpl implements PublicationRequestPersistenceService {

  private final PublicationRequestPersistencePort persistencePort;

  @Override
  @Transactional(readOnly = true)
  public boolean existsByRequestId(String requestId) {
    return persistencePort.existsByRequestId(requestId);
  }

  @Override
  @Transactional(readOnly = true)
  public boolean existsAnyByRequestId(List<String> requestIds) {
    return persistencePort.existsAnyByRequestIds(requestIds);
  }

  @Override
  @Transactional(readOnly = true)
  public boolean existsAllByRequestId(List<String> requestIds) {
    return persistencePort.existsAllByRequestIds(requestIds);
  }

  @Override
  @Transactional
  public PublicationRequest insert(PublicationRequest request) {
    if (existsByRequestId(request.requestId())) {
      throw new IngestionException(IngestionErrorCode.DUPLICATE_RESOURCE, duplicateMessage(request));
    }

    return persistencePort.insert(request);
  }

  @Override
  @Transactional
  public List<PublicationRequest> insertAll(List<PublicationRequest> requests) {
    List<String> requestIds = requests.stream().map(PublicationRequest::requestId).toList();
    if (existsAnyByRequestId(requestIds)) {
      throw new IngestionException(IngestionErrorCode.DUPLICATE_RESOURCE, "Publication Request already exists.");
    }

    return persistencePort.insertAll(requests);
  }

  @Override
  public PublicationRequest markSent(String requestId, Instant publishedAt) {
    PublicationRequest request = findByRequestId(requestId).orElseThrow(() ->
      new IngestionException(IngestionErrorCode.RESOURCE_NOT_FOUND, "Publication Request not found. id=" + requestId));

    request.markSent(publishedAt);
    return persistencePort.update(request);
  }

  @Override
  public PublicationRequest markFailed(String requestId, String failureReason, Instant failedAt) {
    PublicationRequest request = findByRequestId(requestId).orElseThrow(() ->
      new IngestionException(IngestionErrorCode.RESOURCE_NOT_FOUND, "Publication Request not found. id=" + requestId));

    request.markFailed(failureReason, failedAt);
    return persistencePort.update(request);
  }

  @Override
  public PublicationRequest retry(String requestId, Instant requestedAt) {
    PublicationRequest request = findByRequestId(requestId).orElseThrow(() ->
      new IngestionException(IngestionErrorCode.RESOURCE_NOT_FOUND, "Publication Request not found. id=" + requestId));

    request.retry(requestedAt);
    return persistencePort.update(request);
  }

  @Override
  public Optional<PublicationRequest> findByRequestId(String requestId) {
    return persistencePort.findByRequestId(requestId);
  }

  private static String duplicateMessage(PublicationRequest request) {
    return "Publication Request already exists. id=" + request.requestId() + ", status=" + request.status();
  }

  private static String notFoundMessage(PublicationRequest request) {
    return "Publication Request not found. id=" + request.requestId() + ", status=" + request.status();
  }

}
