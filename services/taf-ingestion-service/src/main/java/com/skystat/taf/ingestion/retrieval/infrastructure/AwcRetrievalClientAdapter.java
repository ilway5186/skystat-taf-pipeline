package com.skystat.taf.ingestion.retrieval.infrastructure;

import com.skystat.taf.ingestion.common.annotation.Adapter;
import com.skystat.taf.ingestion.retrieval.application.dto.RetrievalResult;
import com.skystat.taf.ingestion.retrieval.application.port.RetrievalClientPort;
import com.skystat.taf.ingestion.retrieval.domain.RetrievalFailureReason;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.net.SocketTimeoutException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Adapter
@Profile("sync")
@RequiredArgsConstructor
public class AwcRetrievalClientAdapter implements RetrievalClientPort {

  private static final String EXTERNAL_API_URL = "https://aviationweather.gov/api/data/taf";
  private static final String ACCEPT_HEADER = "application/json";
  private static final String RESPONSE_FORMAT = "json";
  private static final int CHUNK_SIZE = 20;
  private static final ParameterizedTypeReference<List<AwcTafResponse>> AWC_TAF_RESPONSE_LIST =
    new ParameterizedTypeReference<>() {
    };

  private final RestClient restClient;

  @Override
  public RetrievalResult retrieve(String icao) {
    try {
      List<AwcTafResponse> responses = fetch(List.of(icao));

      AwcTafResponse response = responses.stream()
        .filter(candidate -> icao.equalsIgnoreCase(candidate.icaoId()))
        .findFirst()
        .orElse(null);

      if (response == null) {
        return RetrievalResult.failure(icao, RetrievalFailureReason.EMPTY_RESPONSE, "AWC returned no TAF report.");
      }

      String reportText = response.rawTAF();
      if (reportText == null || reportText.isBlank()) {
        return RetrievalResult.failure(icao, RetrievalFailureReason.EMPTY_RESPONSE, "AWC returned a blank rawTAF.");
      }

      return RetrievalResult.success(icao, reportText);
    } catch (Exception e) {
      return RetrievalResult.failure(icao, failureReason(e), e.getMessage());
    }
  }

  @Override
  public List<RetrievalResult> retrieve(List<String> icaos, int concurrency) {
    List<List<String>> chunks = chunk(icaos);
    int permits = Math.max(1, concurrency);

    try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
      Semaphore semaphore = new Semaphore(permits);

      List<Future<List<RetrievalResult>>> futures = chunks.stream()
        .map(chunk -> executor.submit(() -> {
          semaphore.acquire();
          try {
            return retrieveChunk(chunk);
          } finally {
            semaphore.release();
          }
        }))
        .toList();

      return futures.stream()
        .flatMap(future -> get(future).stream())
        .toList();
    }
  }

  private List<AwcTafResponse> fetch(List<String> icaos) {
    List<AwcTafResponse> responses = restClient.get()
      .uri(uri(icaos))
      .header("Accept", ACCEPT_HEADER)
      .retrieve()
      .body(AWC_TAF_RESPONSE_LIST);

    return responses == null ? List.of() : responses;
  }

  private static List<List<String>> chunk(List<String> values) {
    List<List<String>> chunks = new ArrayList<>();

    for (int start = 0; start < values.size(); start += CHUNK_SIZE) {
      int end = Math.min(start + CHUNK_SIZE, values.size());
      chunks.add(values.subList(start, end));
    }

    return chunks;
  }

  private static <T> T get(Future<T> future) {
    try {
      return future.get();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IllegalStateException("Retrieval task was interrupted.", e);
    } catch (ExecutionException e) {
      throw new IllegalStateException("Retrieval task failed.", e.getCause());
    }
  }

  private List<RetrievalResult> retrieveChunk(List<String> icaos) {
    try {
      List<AwcTafResponse> responses = fetch(icaos);
      Map<String, AwcTafResponse> responseByIcao = responses.stream()
        .collect(Collectors.toMap(
          response -> response.icaoId().toUpperCase(Locale.ROOT),
          response -> response,
          (existingResponse, duplicateResponse) -> existingResponse
        ));

      List<RetrievalResult> result = new ArrayList<>(icaos.size());
      for (String icao : icaos) {
        AwcTafResponse response = responseByIcao.get(icao.toUpperCase(Locale.ROOT));

        if (response == null) {
          result.add(RetrievalResult.failure(icao, RetrievalFailureReason.EMPTY_RESPONSE, "AWC returned no TAF report."));
          continue;
        }

        String reportText = response.rawTAF();
        if (reportText == null || reportText.isBlank()) {
          result.add(RetrievalResult.failure(icao, RetrievalFailureReason.EMPTY_RESPONSE, "AWC returned a blank rawTAF."));
          continue;
        }

        result.add(RetrievalResult.success(icao, reportText));
      }

      return result;
    } catch (Exception e) {
      RetrievalFailureReason failureReason = failureReason(e);
      String failureDetail = e.getMessage();

      return icaos.stream()
        .map(icao -> RetrievalResult.failure(icao, failureReason, failureDetail))
        .toList();
    }
  }

  private static URI uri(List<String> icaos) {
    String joinedIcaos = String.join(",", icaos);
    return URI.create(EXTERNAL_API_URL + "?ids=" + joinedIcaos + "&format=" + RESPONSE_FORMAT);
  }

  private static RetrievalFailureReason failureReason(Throwable error) {
    if (hasCause(error, SocketTimeoutException.class) || hasCause(error, TimeoutException.class)) {
      return RetrievalFailureReason.REQUEST_TIMEOUT;
    }
    if (error instanceof ResourceAccessException) {
      return RetrievalFailureReason.CONNECTION_FAILED;
    }
    if (error instanceof RestClientResponseException responseException) {
      if (responseException.getStatusCode().is4xxClientError()) {
        return RetrievalFailureReason.HTTP_CLIENT_ERROR;
      }
      if (responseException.getStatusCode().is5xxServerError()) {
        return RetrievalFailureReason.HTTP_SERVER_ERROR;
      }
    }
    return RetrievalFailureReason.UNKNOWN;
  }

  private static boolean hasCause(Throwable error, Class<? extends Throwable> causeType) {
    Throwable current = error;
    while (current != null) {
      if (causeType.isInstance(current)) {
        return true;
      }
      current = current.getCause();
    }
    return false;
  }

  private record AwcTafResponse(String icaoId, String rawTAF) {
  }

}
