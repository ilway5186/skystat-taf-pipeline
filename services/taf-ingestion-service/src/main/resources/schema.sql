

CREATE TABLE IF NOT EXISTS retrievals (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  group_id VARCHAR(16) NOT NULL,
  icao VARCHAR(4) NOT NULL,
  report_text TEXT NULL,
  requested_at TIMESTAMP(6) NOT NULL,
  retrieved_at TIMESTAMP(6) NULL,
  status VARCHAR(32) NOT NULL,
  failure_reason VARCHAR(64) NULL,
  failure_detail TEXT NULL,
  attempt_count INT NOT NULL,
  created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),

  UNIQUE KEY uq_retrieval_attempt (group_id, attempt_count),

  INDEX idx_retrieval_icao (icao),
  INDEX idx_retrieval_status_requested_at (status, requested_at)
);
