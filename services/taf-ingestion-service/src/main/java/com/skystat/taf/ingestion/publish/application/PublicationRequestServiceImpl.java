package com.skystat.taf.ingestion.publish.application;

import com.skystat.taf.ingestion.common.policy.IdGenerationPolicy;
import com.skystat.taf.ingestion.publish.application.port.PublicationPersistencePort;
import com.skystat.taf.ingestion.publish.application.service.PublicationRequestService;
import com.skystat.taf.ingestion.publish.domain.Publication;
import com.skystat.taf.ingestion.publish.domain.vo.TafPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class PublicationRequestServiceImpl implements PublicationRequestService {

  private final PublicationPersistencePort persistencePort;
  private final IdGenerationPolicy idGenerationPolicy;

  @Override
  public Publication request(String tafId, TafPayload payload, Instant requestedAt) {
    String publicationId = idGenerationPolicy.generate();
    Publication publication = Publication.pending(publicationId, tafId, payload, requestedAt, 5);

    return persistencePort.insert(publication);
  }

}
