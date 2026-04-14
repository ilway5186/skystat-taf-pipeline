package com.skystat.taf.ingestion.retrieval.application.port;

import com.skystat.taf.ingestion.retrieval.application.dto.GroupIdAndAttemptCount;
import com.skystat.taf.ingestion.retrieval.domain.Retrieval;

import java.util.List;
import java.util.Optional;

public interface RetrievalPersistencePort {

  boolean existsByGroupIdAndAttemptCount(String groupId, int attemptCount);

  boolean existsAnyByGroupIdAndAttemptCount(List<GroupIdAndAttemptCount> pairs);

  boolean existsAllByGroupIdAndAttemptCount(List<GroupIdAndAttemptCount> pairs);

  Retrieval insert(Retrieval retrieval);

  Retrieval update(Retrieval retrieval);

  List<Retrieval> insertAll(List<Retrieval> retrievals);

  List<Retrieval> updateAll(List<Retrieval> retrievals);

  Optional<Retrieval> findByGroupIdAndAttemptCount(String groupId, int attemptCount);

  List<Retrieval> findAllByGroupId(String groupId);

}
