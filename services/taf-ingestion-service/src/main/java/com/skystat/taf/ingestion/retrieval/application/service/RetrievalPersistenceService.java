package com.skystat.taf.ingestion.retrieval.application.service;

import com.skystat.taf.ingestion.retrieval.domain.Retrieval;

import java.util.List;
import java.util.Optional;

public interface RetrievalPersistenceService {

  boolean existsByGroupIdAndAttemptCount(String groupId, int attemptCount);

  Retrieval insert(Retrieval retrieval);

  Retrieval update(Retrieval retrieval);

  List<Retrieval> insertAll(List<Retrieval> retrievals);

  List<Retrieval> updateAll(List<Retrieval> retrievals);

  Optional<Retrieval> findByGroupIdAndAttemptCount(String groupId, int attemptCount);

  List<Retrieval> findAllByGroupId(String groupId);

}
