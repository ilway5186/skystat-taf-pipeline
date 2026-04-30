package com.skystat.taf.ingestion.storage.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@Profile("sync")
@RequiredArgsConstructor
public class PublicationRequestJdbcRepository {

  private final JdbcTemplate jdbcTemplate;

  public boolean existsAnyByRequestIds(List<String> requestIds) {
    if (requestIds == null || requestIds.isEmpty()) {
      return false;
    }

    String placeHolders = requestIds.stream()
      .map(r -> "?")
      .collect(Collectors.joining(", "));

    String sql = """
      SELECT EXISTS (
        SELECT 1 FROM publication_request pr WHERE pr.request_id IN (%s)
      )
      """.formatted(placeHolders);

    Integer result = jdbcTemplate.queryForObject(sql, Integer.class, requestIds.toArray());
    return result != null && result == 1;
  }

  public boolean existsAllByRequestIds(List<String> requestIds) {
    if (requestIds == null || requestIds.isEmpty()) {
      return false;
    }

    String placeHolders = requestIds.stream()
      .map(r -> "?")
      .collect(Collectors.joining(", "));

    String sql = """
      SELECT COUNT(DISTINCT request_id)
      FROM publication_request pr
      WHERE pr.request_id IN (%s)
      """.formatted(placeHolders);

    Integer result = jdbcTemplate.queryForObject(sql, Integer.class, requestIds.toArray());
    return result != null && result == requestIds.size();
  }


}
