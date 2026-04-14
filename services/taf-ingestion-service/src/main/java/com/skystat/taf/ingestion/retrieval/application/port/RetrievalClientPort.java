package com.skystat.taf.ingestion.retrieval.application.port;

import com.skystat.taf.ingestion.retrieval.application.dto.RetrievalResult;

import java.util.List;

public interface RetrievalClientPort {

  RetrievalResult retrieve(String icao);
  List<RetrievalResult> retrieve(List<String> icaos, int concurrency);

}
