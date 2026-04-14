package com.skystat.taf.ingestion.common.config;

import com.skystat.taf.ingestion.retrieval.infrastructure.mysql.RetrievalJpaRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@Profile("sync")
@EnableJpaAuditing
@EnableJpaRepositories(basePackageClasses = RetrievalJpaRepository.class)
public class JpaConfig {
}
