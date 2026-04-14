package com.skystat.taf.ingestion.retrieval.application;

import com.skystat.taf.ingestion.retrieval.application.dto.RetrievalResult;
import com.skystat.taf.ingestion.retrieval.application.port.RetrievalClientPort;
import com.skystat.taf.ingestion.retrieval.application.service.RetrievalClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Profile("sync")
@RequiredArgsConstructor
public class RetrievalClientServiceImpl implements RetrievalClientService {

  private final RetrievalClientPort retrievalClientPort;

  @Override
  public RetrievalResult retrieve(String icao) {
    return retrievalClientPort.retrieve(icao);
  }

  @Override
  public List<RetrievalResult> retrieve(List<String> icaos, int concurrency) {
    return retrievalClientPort.retrieve(icaos, concurrency);
  }

}
