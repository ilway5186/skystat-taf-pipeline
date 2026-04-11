package com.skystat.taf.ingestion.retrieval.application;

import com.skystat.taf.ingestion.retrieval.domain.Retrieval;

import java.util.Optional;

public interface RetrievalPersistencePort {

  void save(Retrieval retrieval);

  Optional<Retrieval> findByRetrievalId(String retrievalId);

}
