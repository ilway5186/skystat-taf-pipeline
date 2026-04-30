package com.skystat.taf.ingestion.retrieval.infrastructure.mysql;

import com.skystat.taf.ingestion.retrieval.application.dto.GroupIdAndAttemptCount;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@Profile("sync")
@RequiredArgsConstructor
public class RetrievalJdbcRepository {

  private final JdbcTemplate jdbcTemplate;

  public boolean existsAnyByGroupIdAndAttemptCount(List<GroupIdAndAttemptCount> pairs) {
    if (pairs == null || pairs.isEmpty()) {
      return false;
    }

    String placeHolders = pairs.stream().map(p -> "(?, ?)").collect(Collectors.joining(", "));
    String sql = """
      SELECT EXISTS (
        SELECT 1 FROM retrieval r WHERE (r.group_id, r.attempt_count) IN (%s)
      )
      """.formatted(placeHolders);

    List<Object> params = new ArrayList<>();
    for (GroupIdAndAttemptCount pair : pairs) {
      params.add(pair.groupId());
      params.add(pair.attemptCount());
    }

    Integer result = jdbcTemplate.queryForObject(sql, Integer.class, params.toArray());
    return result != null && result == 1;
  }

  public boolean existsAllByGroupIdAndAttemptCount(List<GroupIdAndAttemptCount> pairs) {
    if (pairs == null || pairs.isEmpty()) {
      return true;
    }

    String placeHolders = pairs.stream().map(p -> "(?, ?)").collect(Collectors.joining(", "));
    String sql = """
      SELECT COUNT(DISTINCT r.group_id, r.attempt_count)
      FROM retrieval r
      WHERE (r.group_id, r.attempt_count) IN (%s)
      """.formatted(placeHolders);

    List<Object> params = new ArrayList<>();
    for (GroupIdAndAttemptCount pair : pairs) {
      params.add(pair.groupId());
      params.add(pair.attemptCount());
    }

    Integer matchedCount = jdbcTemplate.queryForObject(sql, Integer.class, params.toArray());
    return matchedCount != null && matchedCount == pairs.size();
  }

  public Map<GroupIdAndAttemptCount, Long> findIdsByGroupIdAndAttemptCount(List<GroupIdAndAttemptCount> pairs) {
    if (pairs == null || pairs.isEmpty()) {
      return Map.of();
    }

    String placeHolders = pairs.stream().map(p -> "(?, ?)").collect(Collectors.joining(", "));
    String sql = """
      SELECT r.id, r.group_id, r.attempt_count
      FROM retrieval r
      WHERE (r.group_id, r.attempt_count) IN (%s)
      """.formatted(placeHolders);

    List<Object> params = new ArrayList<>();
    for (GroupIdAndAttemptCount pair : pairs) {
      params.add(pair.groupId());
      params.add(pair.attemptCount());
    }

    return jdbcTemplate.query(sql, resultSet -> {
      Map<GroupIdAndAttemptCount, Long> ids = new HashMap<>();
      while (resultSet.next()) {
        GroupIdAndAttemptCount pair = new GroupIdAndAttemptCount(
          resultSet.getString("group_id"),
          resultSet.getInt("attempt_count")
        );
        ids.put(pair, resultSet.getLong("id"));
      }
      return ids;
    }, params.toArray());
  }

}
