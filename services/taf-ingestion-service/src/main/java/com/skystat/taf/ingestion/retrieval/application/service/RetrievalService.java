package com.skystat.taf.ingestion.retrieval.application.service;

import com.skystat.taf.ingestion.retrieval.application.dto.RetrievalServiceResult;
import com.skystat.taf.ingestion.retrieval.domain.Retrieval;

import java.util.List;

public interface RetrievalService {

  RetrievalServiceResult retrieve(String icao);
  List<RetrievalServiceResult> retrieve(List<String> icao, int concurrency);

  RetrievalServiceResult retry(Retrieval retrieval);
  List<RetrievalServiceResult> retry(List<Retrieval> retrievals, int concurrency);

}
