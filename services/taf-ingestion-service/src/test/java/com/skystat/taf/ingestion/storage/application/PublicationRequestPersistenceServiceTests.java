package com.skystat.taf.ingestion.storage.application;

import com.skystat.taf.ingestion.common.exception.IngestionErrorCode;
import com.skystat.taf.ingestion.common.exception.IngestionException;
import com.skystat.taf.ingestion.storage.application.port.PublicationRequestPersistencePort;
import com.skystat.taf.ingestion.storage.application.service.PublicationRequestPersistenceService;
import com.skystat.taf.ingestion.storage.domain.PublicationRequest;
import com.skystat.taf.ingestion.storage.domain.PublicationRequestStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PublicationRequestPersistenceServiceTests {

  private PublicationRequestPersistenceService service;
  private FakePublicationRequestPersistencePort persistencePort;

  @BeforeEach
  void setUp() {
    persistencePort = new FakePublicationRequestPersistencePort();
    service = new PublicationRequestPersistenceServiceImpl(persistencePort);
  }

  @Test
  void requestId로_발행요청_존재여부를_확인할_수_있다() {
    assertTrue(service.existsByRequestId("1"));
    assertFalse(service.existsByRequestId("unknown"));
  }

  @Test
  void requestId로_발행요청을_조회할_수_있다() {
    Optional<PublicationRequest> result = service.findByRequestId("1");

    assertTrue(result.isPresent());
    assertEquals("1", result.get().requestId());
  }

  @Test
  void 새로운_발행요청을_insert할_수_있다() {
    PublicationRequest request = PublicationRequest.pending(
      "4",
      "taf-4",
      Instant.parse("2026-04-23T07:01:00Z")
    );

    PublicationRequest savedRequest = service.insert(request);

    assertEquals(request, savedRequest);
    assertTrue(service.existsByRequestId("4"));
  }

  @Test
  void 중복된_requestId로_insert를_시도하면_예외가_발생한다() {
    PublicationRequest request = PublicationRequest.pending(
      "1",
      "taf-1",
      Instant.parse("2026-04-23T07:01:00Z")
    );

    IngestionException exception = assertThrows(IngestionException.class, () -> service.insert(request));

    assertEquals(IngestionErrorCode.DUPLICATE_RESOURCE, exception.errorCode());
  }

  @Test
  void 여러_발행요청을_insertAll할_수_있다() {
    PublicationRequest request4 = PublicationRequest.pending(
      "4",
      "taf-4",
      Instant.parse("2026-04-23T07:01:00Z")
    );
    PublicationRequest request5 = PublicationRequest.pending(
      "5",
      "taf-5",
      Instant.parse("2026-04-23T07:01:03Z")
    );

    List<PublicationRequest> savedRequests = service.insertAll(List.of(request4, request5));

    assertEquals(2, savedRequests.size());
    assertTrue(service.existsAllByRequestId(List.of("4", "5")));
  }

  @Test
  void insertAll_대상중_이미_존재하는_requestId가_있으면_예외가_발생한다() {
    PublicationRequest request1 = PublicationRequest.pending(
      "1",
      "taf-1",
      Instant.parse("2026-04-23T07:01:00Z")
    );
    PublicationRequest request4 = PublicationRequest.pending(
      "4",
      "taf-4",
      Instant.parse("2026-04-23T07:01:03Z")
    );

    IngestionException exception = assertThrows(
      IngestionException.class,
      () -> service.insertAll(List.of(request1, request4))
    );

    assertEquals(IngestionErrorCode.DUPLICATE_RESOURCE, exception.errorCode());
  }

  @Test
  void pending_발행요청을_sent로_변경할_수_있다() {
    Instant publishedAt = Instant.parse("2026-04-23T07:02:00Z");

    PublicationRequest request = service.markSent("1", publishedAt);

    assertEquals(PublicationRequestStatus.SENT, request.status());
    assertEquals(publishedAt, request.publishedAt());
  }

  @Test
  void pending_발행요청을_failed로_변경할_수_있다() {
    Instant failedAt = Instant.parse("2026-04-23T07:02:00Z");

    PublicationRequest request = service.markFailed("1", "Publish failed.", failedAt);

    assertEquals(PublicationRequestStatus.FAILED, request.status());
    assertEquals("Publish failed.", request.failureReason());
    assertEquals(failedAt, request.failedAt());
  }

  @Test
  void failed_발행요청을_retry할_수_있다() {
    Instant requestedAt = Instant.parse("2026-04-23T07:03:00Z");

    PublicationRequest request = service.retry("3", requestedAt);

    assertEquals(PublicationRequestStatus.PENDING, request.status());
    assertEquals(requestedAt, request.requestedAt());
    assertEquals(1, request.retryCount());
  }

  @Test
  void 존재하지_않는_requestId를_markSent하면_예외가_발생한다() {
    IngestionException exception = assertThrows(
      IngestionException.class,
      () -> service.markSent("unknown", Instant.parse("2026-04-23T07:02:00Z"))
    );

    assertEquals(IngestionErrorCode.RESOURCE_NOT_FOUND, exception.errorCode());
  }

  @Test
  void 존재하지_않는_requestId를_markFailed하면_예외가_발생한다() {
    IngestionException exception = assertThrows(
      IngestionException.class,
      () -> service.markFailed("unknown", "Publish failed.", Instant.parse("2026-04-23T07:02:00Z"))
    );

    assertEquals(IngestionErrorCode.RESOURCE_NOT_FOUND, exception.errorCode());
  }

  @Test
  void 존재하지_않는_requestId를_retry하면_예외가_발생한다() {
    IngestionException exception = assertThrows(
      IngestionException.class,
      () -> service.retry("unknown", Instant.parse("2026-04-23T07:03:00Z"))
    );

    assertEquals(IngestionErrorCode.RESOURCE_NOT_FOUND, exception.errorCode());
  }

  static class FakePublicationRequestPersistencePort implements PublicationRequestPersistencePort {

    private final List<PublicationRequest> requests = new ArrayList<>();

    FakePublicationRequestPersistencePort() {
      PublicationRequest request1 = PublicationRequest.pending("1", "taf-1", Instant.parse("2026-04-23T07:00:00Z"));
      PublicationRequest request2 = PublicationRequest.pending("2", "taf-2", Instant.parse("2026-04-23T07:00:03Z"));
      PublicationRequest request3 = PublicationRequest.pending("3", "taf-3", Instant.parse("2026-04-23T07:00:05Z"));

      request2.markSent(Instant.parse("2026-04-23T07:00:07Z"));
      request3.markFailed("Internal Error", Instant.parse("2026-04-23T07:00:08Z"));

      requests.add(request1);
      requests.add(request2);
      requests.add(request3);
    }

    @Override
    public boolean existsByRequestId(String requestId) {
      return requests.stream().anyMatch(request -> request.requestId().equals(requestId));
    }

    @Override
    public boolean existsAnyByRequestId(List<String> requestIds) {
      return requests.stream().anyMatch(request -> requestIds.contains(request.requestId()));
    }

    @Override
    public boolean existsAllByRequestId(List<String> requestIds) {
      return requestIds.stream().allMatch(this::existsByRequestId);
    }

    @Override
    public PublicationRequest insert(PublicationRequest request) {
      requests.add(request);
      return request;
    }

    @Override
    public PublicationRequest update(PublicationRequest request) {
      int index = indexOf(request.requestId());
      if (index < 0) {
        throw new IngestionException(
          IngestionErrorCode.RESOURCE_NOT_FOUND,
          "Publication Request not found. id=" + request.requestId()
        );
      }

      requests.set(index, request);
      return request;
    }

    @Override
    public List<PublicationRequest> insertAll(List<PublicationRequest> requests) {
      this.requests.addAll(requests);
      return List.copyOf(requests);
    }

    @Override
    public List<PublicationRequest> updateAll(List<PublicationRequest> requests) {
      return requests.stream().map(this::update).toList();
    }

    @Override
    public Optional<PublicationRequest> findByRequestId(String requestId) {
      return requests.stream()
        .filter(request -> request.requestId().equals(requestId))
        .findFirst();
    }

    private int indexOf(String requestId) {
      for (int i=0; i<requests.size(); i++) {
        if (requests.get(i).requestId().equals(requestId)) {
          return i;
        }
      }
      return -1;
    }
  }
}
