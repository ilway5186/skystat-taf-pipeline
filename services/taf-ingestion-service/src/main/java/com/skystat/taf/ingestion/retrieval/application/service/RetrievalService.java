package com.skystat.taf.ingestion.retrieval.application.service;

import com.skystat.taf.ingestion.retrieval.domain.Retrieval;

import java.util.List;

public interface RetrievalService {

  Retrieval retrieve(String icao);
  List<Retrieval> retrieve(List<String> icao, int concurrency);

  Retrieval retry(Retrieval retrieval);
  List<Retrieval> retry(List<Retrieval> retrievals, int concurrency);

}
