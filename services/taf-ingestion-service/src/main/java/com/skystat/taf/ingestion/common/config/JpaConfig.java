package com.skystat.taf.ingestion.common.config;

import com.skystat.taf.ingestion.retrieval.infrastructure.mysql.RetrievalJpaRepository;
import com.skystat.taf.ingestion.storage.infrastructure.TafStorageJpaRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@Profile("sync")
@EnableJpaAuditing
public class JpaConfig {
}
