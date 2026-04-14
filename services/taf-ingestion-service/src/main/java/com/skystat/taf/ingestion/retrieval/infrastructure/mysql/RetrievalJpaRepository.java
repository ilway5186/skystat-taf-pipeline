package com.skystat.taf.ingestion.retrieval.infrastructure.mysql;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RetrievalJpaRepository extends JpaRepository<RetrievalEntity, Long> {

  boolean existsByGroupIdAndAttemptCount(String groupId, int attemptCount);

  Optional<RetrievalEntity> findByGroupIdAndAttemptCount(String groupId, int attemptCount);

  List<RetrievalEntity> findAllByGroupIdOrderByAttemptCountAsc(String groupId);



}
