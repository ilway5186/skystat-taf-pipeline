package com.skystat.taf.ingestion.publish.application.service;

import com.skystat.taf.ingestion.publish.domain.Publication;

public interface PublicationDispatchService {

  void publish(Publication publication);

}
