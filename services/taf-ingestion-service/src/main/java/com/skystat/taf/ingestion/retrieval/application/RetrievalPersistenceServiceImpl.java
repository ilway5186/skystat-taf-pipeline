package com.skystat.taf.ingestion.retrieval.application;

import com.skystat.taf.ingestion.common.exception.IngestionException;
import com.skystat.taf.ingestion.retrieval.application.dto.GroupIdAndAttemptCount;
import com.skystat.taf.ingestion.retrieval.application.port.RetrievalPersistencePort;
import com.skystat.taf.ingestion.retrieval.application.service.RetrievalPersistenceService;
import com.skystat.taf.ingestion.retrieval.domain.Retrieval;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.skystat.taf.ingestion.common.exception.IngestionErrorCode.DUPLICATE_RESOURCE;
import static com.skystat.taf.ingestion.common.exception.IngestionErrorCode.RESOURCE_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Profile("sync")
public class RetrievalPersistenceServiceImpl implements RetrievalPersistenceService {

  private final RetrievalPersistencePort retrievalPersistencePort;

  @Override
  @Transactional(readOnly = true)
  public boolean existsByGroupIdAndAttemptCount(String groupId, int attemptCount) {
    return retrievalPersistencePort.existsByGroupIdAndAttemptCount(groupId, attemptCount);
  }

  @Override
  @Transactional
  public Retrieval insert(Retrieval retrieval) {
    if (existsByGroupIdAndAttemptCount(retrieval.groupId(), retrieval.attemptCount())) {
      throw new IngestionException(DUPLICATE_RESOURCE, duplicateMessage(retrieval));
    }

    return retrievalPersistencePort.insert(retrieval);
  }

  @Override
  @Transactional
  public Retrieval update(Retrieval retrieval) {
    if (!existsByGroupIdAndAttemptCount(retrieval.groupId(), retrieval.attemptCount())) {
      throw new IngestionException(RESOURCE_NOT_FOUND, notFoundMessage(retrieval));
    }

    return retrievalPersistencePort.update(retrieval);
  }

  @Override
  @Transactional
  public List<Retrieval> insertAll(List<Retrieval> retrievals) {
    List<GroupIdAndAttemptCount> pairs = retrievals.stream()
      .map(retrieval -> new GroupIdAndAttemptCount(retrieval.groupId(), retrieval.attemptCount()))
      .toList();

    if (retrievalPersistencePort.existsAnyByGroupIdAndAttemptCount(pairs)) {
      throw new IngestionException(DUPLICATE_RESOURCE, "Retrieval already exists.");
    }

    return retrievalPersistencePort.insertAll(retrievals);
  }

  @Override
  @Transactional
  public List<Retrieval> updateAll(List<Retrieval> retrievals) {
    List<GroupIdAndAttemptCount> pairs = retrievals.stream()
      .map(retrieval -> new GroupIdAndAttemptCount(retrieval.groupId(), retrieval.attemptCount()))
      .toList();

    if (!retrievalPersistencePort.existsAllByGroupIdAndAttemptCount(pairs)) {
      throw new IngestionException(RESOURCE_NOT_FOUND, "Retrieval not found.");
    }

    return retrievalPersistencePort.updateAll(retrievals);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<Retrieval> findByGroupIdAndAttemptCount(String groupId, int attemptCount) {
    return retrievalPersistencePort.findByGroupIdAndAttemptCount(groupId, attemptCount);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Retrieval> findAllByGroupId(String groupId) {
    return retrievalPersistencePort.findAllByGroupId(groupId);
  }

  private static String duplicateMessage(Retrieval retrieval) {
    return "Retrieval already exists. groupId=" + retrieval.groupId() + ", attemptCount=" + retrieval.attemptCount();
  }

  private static String notFoundMessage(Retrieval retrieval) {
    return "Retrieval not found. groupId=" + retrieval.groupId() + ", attemptCount=" + retrieval.attemptCount();
  }

}
