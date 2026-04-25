package com.skystat.taf.ingestion.publish.application.port;

import com.skystat.taf.ingestion.publish.domain.Publication;

import java.util.List;
import java.util.Optional;

public interface PublicationPersistencePort {

  Publication insert(Publication publication);

  Publication update(Publication publication);

  List<Publication> insertAll(List<Publication> publications);

  List<Publication> findPublishable(int limit);

  Optional<Publication> findByPublicationId(String publicationId);

}
