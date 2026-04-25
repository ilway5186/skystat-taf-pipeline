package com.skystat.taf.ingestion.publish.domain;

import com.skystat.taf.ingestion.common.exception.IngestionException;
import com.skystat.taf.ingestion.publish.domain.vo.TafPayload;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

public class PublicationTests {

  private static final String PUBLICATION_ID = "publication-id";
  private static final String TAF_ID = "taf-id";
  private static final TafPayload PAYLOAD = TafPayload.of(TAF_ID, "RKSI", "TAF RKSI 250500Z ...");
  private static final Instant CREATED_AT = Instant.parse("2026-04-25T00:00:00Z");

  @Test
  void pending_상태의_발행_객체를_생성할_수_있다() {
    Publication publication = Publication.pending(PUBLICATION_ID, TAF_ID, PAYLOAD, CREATED_AT, 3);

    assertEquals(PUBLICATION_ID, publication.publicationId());
    assertEquals(TAF_ID, publication.tafId());
    assertEquals(PAYLOAD, publication.payload());
    assertEquals(PublicationStatus.PENDING, publication.status());
    assertEquals(CREATED_AT, publication.createdAt());
    assertEquals(0, publication.attemptCount());
    assertEquals(3, publication.maxAttemptCount());
    assertTrue(publication.canPublish());
    assertEquals("TAF_PUBLISHED:" + TAF_ID, publication.idempotencyKey());
  }

  @Test
  void 필수값이_비어있으면_발행_객체를_생성할_수_없다() {
    assertAll(
      () -> assertThrows(IngestionException.class, () -> Publication.pending("", TAF_ID, PAYLOAD, CREATED_AT, 3)),
      () -> assertThrows(IngestionException.class, () -> Publication.pending(PUBLICATION_ID, "", PAYLOAD, CREATED_AT, 3)),
      () -> assertThrows(IngestionException.class, () -> Publication.pending(PUBLICATION_ID, TAF_ID, null, CREATED_AT, 3)),
      () -> assertThrows(IngestionException.class, () -> Publication.pending(PUBLICATION_ID, TAF_ID, PAYLOAD, null, 3)),
      () -> assertThrows(IngestionException.class, () -> Publication.pending(PUBLICATION_ID, TAF_ID, PAYLOAD, CREATED_AT, 0))
    );
  }

  @Test
  void 발행_시도를_시작하면_publishing_상태가_되고_시도횟수가_증가한다() {
    Publication publication = Publication.pending(PUBLICATION_ID, TAF_ID, PAYLOAD, CREATED_AT, 3);
    Instant attemptedAt = Instant.parse("2026-04-25T00:01:00Z");

    publication.markPublishing(attemptedAt);

    assertEquals(PublicationStatus.PUBLISHING, publication.status());
    assertEquals(attemptedAt, publication.lastAttemptedAt());
    assertEquals(1, publication.attemptCount());
    assertFalse(publication.canPublish());
  }

  @Test
  void publishing_상태에서_발행_성공으로_전이할_수_있다() {
    Publication publication = Publication.pending(PUBLICATION_ID, TAF_ID, PAYLOAD, CREATED_AT, 3);
    publication.markPublishing(Instant.parse("2026-04-25T00:01:00Z"));

    Instant publishedAt = Instant.parse("2026-04-25T00:02:00Z");
    publication.markPublished(publishedAt);

    assertTrue(publication.isPublished());
    assertEquals(PublicationStatus.PUBLISHED, publication.status());
    assertEquals(publishedAt, publication.publishedAt());
    assertNull(publication.failureReason());
    assertNull(publication.failedAt());
  }

  @Test
  void publishing_상태에서_발행_실패로_전이할_수_있다() {
    Publication publication = Publication.pending(PUBLICATION_ID, TAF_ID, PAYLOAD, CREATED_AT, 3);
    publication.markPublishing(Instant.parse("2026-04-25T00:01:00Z"));

    Instant failedAt = Instant.parse("2026-04-25T00:02:00Z");
    publication.markFailed("RabbitMQ unavailable.", failedAt);

    assertTrue(publication.isFailed());
    assertEquals(PublicationStatus.FAILED, publication.status());
    assertEquals("RabbitMQ unavailable.", publication.failureReason());
    assertEquals(failedAt, publication.failedAt());
    assertFalse(publication.canPublish());
    assertTrue(publication.canRetry());
  }

  @Test
  void failed_상태에서_retry로_재시도할_수_있다() {
    Publication publication = Publication.pending(PUBLICATION_ID, TAF_ID, PAYLOAD, CREATED_AT, 3);
    publication.markPublishing(Instant.parse("2026-04-25T00:01:00Z"));
    publication.markFailed("RabbitMQ unavailable.", Instant.parse("2026-04-25T00:02:00Z"));

    Instant retriedAt = Instant.parse("2026-04-25T00:03:00Z");
    publication.retry(retriedAt);

    assertEquals(PublicationStatus.PUBLISHING, publication.status());
    assertEquals(retriedAt, publication.lastAttemptedAt());
    assertEquals(2, publication.attemptCount());
    assertFalse(publication.canRetry());
  }

  @Test
  void failed_상태에서는_markPublishing으로_재시도할_수_없다() {
    Publication publication = Publication.pending(PUBLICATION_ID, TAF_ID, PAYLOAD, CREATED_AT, 3);
    publication.markPublishing(Instant.parse("2026-04-25T00:01:00Z"));
    publication.markFailed("RabbitMQ unavailable.", Instant.parse("2026-04-25T00:02:00Z"));

    assertThrows(IngestionException.class, () -> publication.markPublishing(Instant.parse("2026-04-25T00:03:00Z")));
  }

  @Test
  void failed_상태가_아니면_retry할_수_없다() {
    Publication publication = Publication.pending(PUBLICATION_ID, TAF_ID, PAYLOAD, CREATED_AT, 3);

    assertThrows(IngestionException.class, () -> publication.retry(Instant.parse("2026-04-25T00:03:00Z")));
  }

  @Test
  void publishing_상태가_아니면_성공이나_실패로_전이할_수_없다() {
    Publication publication = Publication.pending(PUBLICATION_ID, TAF_ID, PAYLOAD, CREATED_AT, 3);

    assertAll(
      () -> assertThrows(IngestionException.class, () -> publication.markPublished(Instant.parse("2026-04-25T00:02:00Z"))),
      () -> assertThrows(IngestionException.class, () -> publication.markFailed("failed", Instant.parse("2026-04-25T00:02:00Z")))
    );
  }

  @Test
  void 최대_시도횟수에_도달하면_더_이상_발행할_수_없다() {
    Publication publication = Publication.pending(PUBLICATION_ID, TAF_ID, PAYLOAD, CREATED_AT, 2);

    publication.markPublishing(Instant.parse("2026-04-25T00:01:00Z"));
    publication.markFailed("first failure", Instant.parse("2026-04-25T00:02:00Z"));
    publication.retry(Instant.parse("2026-04-25T00:03:00Z"));
    publication.markFailed("second failure", Instant.parse("2026-04-25T00:04:00Z"));

    assertTrue(publication.isExhausted());
    assertFalse(publication.canPublish());
    assertFalse(publication.canRetry());
    assertThrows(IngestionException.class, () -> publication.retry(Instant.parse("2026-04-25T00:05:00Z")));
  }

}
