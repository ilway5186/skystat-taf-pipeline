package com.skystat.taf.ingestion.storage.application.service;

import com.skystat.taf.ingestion.storage.domain.PublicationRequest;

import java.util.List;
import java.util.Optional;

public interface PublicationRequestPersistenceService {

  boolean existsByTafId(String tafId);

  PublicationRequest insert(PublicationRequest request);

  PublicationRequest update(PublicationRequest request);

  List<PublicationRequest> insertAll(List<PublicationRequest> requests);

  List<PublicationRequest> updateAll(List<PublicationRequest> requests);

  Optional<PublicationRequest> findByTafId(String tafId);

}
