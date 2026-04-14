package com.skystat.taf.ingestion.retrieval.application.service;

import com.skystat.taf.ingestion.retrieval.application.dto.RetrievalResult;

import java.util.List;

public interface RetrievalClientService {

  RetrievalResult retrieve(String icao);

  List<RetrievalResult> retrieve(List<String> icaos, int concurrency);

}
