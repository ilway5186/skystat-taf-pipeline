package com.skystat.taf.ingestion.retrieval.infrastructure.mysql;

import com.skystat.taf.ingestion.common.annotation.Adapter;
import com.skystat.taf.ingestion.common.exception.IngestionException;
import com.skystat.taf.ingestion.retrieval.application.dto.GroupIdAndAttemptCount;
import com.skystat.taf.ingestion.retrieval.application.port.RetrievalPersistencePort;
import com.skystat.taf.ingestion.retrieval.domain.Retrieval;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.skystat.taf.ingestion.common.exception.IngestionErrorCode.RESOURCE_NOT_FOUND;

@Adapter
@Profile("sync")
@RequiredArgsConstructor
public class RetrievalPersistenceAdapter implements RetrievalPersistencePort {

  private final RetrievalJpaRepository retrievalJpaRepository;
  private final RetrievalJdbcRepository retrievalJdbcRepository;

  @Override
  public boolean existsByGroupIdAndAttemptCount(String groupId, int attemptCount) {
    return retrievalJpaRepository.existsByGroupIdAndAttemptCount(groupId, attemptCount);
  }

  @Override
  public boolean existsAnyByGroupIdAndAttemptCount(List<GroupIdAndAttemptCount> pairs) {
    return retrievalJdbcRepository.existsAnyByGroupIdAndAttemptCount(pairs);
  }

  @Override
  public boolean existsAllByGroupIdAndAttemptCount(List<GroupIdAndAttemptCount> pairs) {
    return retrievalJdbcRepository.existsAllByGroupIdAndAttemptCount(pairs);
  }

  @Override
  public Retrieval insert(Retrieval retrieval) {
    return retrievalJpaRepository
      .save(RetrievalEntity.from(retrieval))
      .toDomain();
  }

  @Override
  public Retrieval update(Retrieval retrieval) {
    RetrievalEntity entity = retrievalJpaRepository
      .findByGroupIdAndAttemptCount(retrieval.groupId(), retrieval.attemptCount())
      .map(existing -> RetrievalEntity.from(retrieval, existing.id()))
      .orElseThrow(() -> new IngestionException(RESOURCE_NOT_FOUND, notFoundMessage(retrieval)));

    return retrievalJpaRepository.save(entity).toDomain();
  }

  @Override
  public List<Retrieval> insertAll(List<Retrieval> retrievals) {
    List<RetrievalEntity> entities = retrievals.stream()
      .map(RetrievalEntity::from)
      .toList();

    return retrievalJpaRepository.saveAll(entities).stream()
      .map(RetrievalEntity::toDomain)
      .toList();
  }

  @Override
  public List<Retrieval> updateAll(List<Retrieval> retrievals) {
    Map<GroupIdAndAttemptCount, Long> idByPair = retrievalJdbcRepository.findIdsByGroupIdAndAttemptCount(
      retrievals.stream()
        .map(retrieval -> new GroupIdAndAttemptCount(retrieval.groupId(), retrieval.attemptCount()))
        .toList()
    );

    List<RetrievalEntity> entities = retrievals.stream()
      .map(retrieval -> RetrievalEntity.from(retrieval, findId(retrieval, idByPair)))
      .toList();

    return retrievalJpaRepository.saveAll(entities).stream()
      .map(RetrievalEntity::toDomain)
      .toList();
  }

  @Override
  public Optional<Retrieval> findByGroupIdAndAttemptCount(String groupId, int attemptCount) {
    return retrievalJpaRepository
      .findByGroupIdAndAttemptCount(groupId, attemptCount)
      .map(RetrievalEntity::toDomain);
  }

  @Override
  public List<Retrieval> findAllByGroupId(String groupId) {
    return retrievalJpaRepository
      .findAllByGroupIdOrderByAttemptCountAsc(groupId).stream()
      .map(RetrievalEntity::toDomain)
      .toList();
  }

  private static String notFoundMessage(Retrieval retrieval) {
    return "Retrieval not found. groupId=" + retrieval.groupId() + ", attemptCount=" + retrieval.attemptCount();
  }

  private static Long findId(Retrieval retrieval, Map<GroupIdAndAttemptCount, Long> idByPair) {
    GroupIdAndAttemptCount pair = new GroupIdAndAttemptCount(retrieval.groupId(), retrieval.attemptCount());
    Long id = idByPair.get(pair);
    if (id == null) {
      throw new IngestionException(RESOURCE_NOT_FOUND, notFoundMessage(retrieval));
    }
    return id;
  }

}
