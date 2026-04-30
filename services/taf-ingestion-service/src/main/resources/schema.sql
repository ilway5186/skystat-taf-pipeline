CREATE TABLE IF NOT EXISTS retrieval (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  group_id VARCHAR(16) NOT NULL,
  icao VARCHAR(4) NOT NULL,
  requested_at TIMESTAMP(6) NOT NULL,
  retrieved_at TIMESTAMP(6) NULL,
  status VARCHAR(32) NOT NULL,
  failure_reason VARCHAR(64) NULL,
  failure_detail TEXT NULL,
  attempt_count INT NOT NULL,
  created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),

  UNIQUE KEY uk_retrieval_attempt (group_id, attempt_count),

  INDEX idx_retrieval_icao (icao),
  INDEX idx_retrieval_status_requested_at (status, requested_at)
);

CREATE TABLE IF NOT EXISTS taf_storage (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    taf_id VARCHAR(16) NOT NULL,
    source_id VARCHAR(16) NOT NULL,
    icao VARCHAR(4) NOT NULL,
    report TEXT NOT NULL,
    status VARCHAR(32) NOT NULL,
    failure_reason VARCHAR(64) NULL,
    stored_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),

    UNIQUE KEY uk_taf_storage_taf_id (taf_id),
    KEY idx_taf_storage_icao_stored_at (icao, stored_at),
    KEY idx_taf_storage_source_id (source_id)
);

CREATE TABLE IF NOT EXISTS publication_request (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    request_id VARCHAR(16) NOT NULL,
    taf_id VARCHAR(64) NOT NULL,
    icao VARCHAR(4) NOT NULL,
    status VARCHAR(32) NOT NULL,
    requested_at TIMESTAMP(6) NOT NULL,
    published_at TIMESTAMP(6) NULL,
    failed_at TIMESTAMP(6) NULL,
    failure_reason VARCHAR(64) NULL,
    retry_count INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),

    UNIQUE KEY uk_publication_request_request_id (request_id),
    KEY idx_publication_request_taf_id (taf_id),
    KEY idx_publication_request_status_requested_at (status, requested_at),
    KEY idx_publication_request_icao (icao)
);