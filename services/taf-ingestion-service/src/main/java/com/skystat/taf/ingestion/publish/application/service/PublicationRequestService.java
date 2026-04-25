package com.skystat.taf.ingestion.publish.application.service;

import com.skystat.taf.ingestion.publish.domain.Publication;
import com.skystat.taf.ingestion.publish.domain.vo.TafPayload;

import java.time.Instant;

public interface PublicationRequestService {

  Publication request(String tafId, TafPayload payload, Instant requestedAt);

}
