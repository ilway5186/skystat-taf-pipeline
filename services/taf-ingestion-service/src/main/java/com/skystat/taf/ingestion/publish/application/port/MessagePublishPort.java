package com.skystat.taf.ingestion.publish.application.port;

import com.skystat.taf.ingestion.publish.domain.vo.TafPayload;

public interface MessagePublishPort {

  void publish(TafPayload payload);

}
