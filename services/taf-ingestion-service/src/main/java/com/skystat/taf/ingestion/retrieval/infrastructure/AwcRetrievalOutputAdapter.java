package com.skystat.taf.ingestion.retrieval.infrastructure;

import com.skystat.taf.ingestion.common.annotation.Adapter;
import com.skystat.taf.ingestion.retrieval.application.RetrievalOutputPort;
import com.skystat.taf.ingestion.retrieval.application.RetrievalResult;
import com.skystat.taf.ingestion.retrieval.domain.RetrievalFailureReason;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@Adapter
public class AwcRetrievalOutputAdapter implements RetrievalOutputPort {

  private static final String EXTERNAL_API_URL = "https://aviationweather.gov/api/data/taf";
  private static final String ACCEPT_HEADER = "application/json";
  private static final String RESPONSE_FORMAT = "json";
  private static final int CHUNK_SIZE = 20;
  private static final Duration RESPONSE_TIMEOUT = Duration.ofSeconds(5);
  private static final ParameterizedTypeReference<List<AwcTafResponse>> AWC_TAF_RESPONSE_LIST =
    new ParameterizedTypeReference<>() {};

  private final WebClient webClient;

  public AwcRetrievalOutputAdapter(WebClient.Builder webClientBuilder) {
    this.webClient = webClientBuilder.build();
  }

  @Override
  public Mono<RetrievalResult> retrieve(String icao) {
    return Mono.defer(() -> fetch(List.of(icao))
      .map(responses -> {
        AwcTafResponse response = responses.stream()
          .filter(candidate -> icao.equalsIgnoreCase(candidate.icaoId()))
          .findFirst()
          .orElse(null);

        if (response == null) {
          return RetrievalResult.failed(icao, RetrievalFailureReason.EMPTY_RESPONSE, "AWC returned no TAF report.");
        }

        String reportText = response.rawTAF();
        if (reportText == null || reportText.isBlank()) {
          return RetrievalResult.failed(icao, RetrievalFailureReason.EMPTY_RESPONSE, "AWC returned a blank rawTAF.");
        }

        return RetrievalResult.succeeded(icao, reportText);
      })
      .onErrorResume(error -> Mono.just(RetrievalResult.failed(icao, failureReason(error), error.getMessage()))));
  }

  @Override
  public Flux<RetrievalResult> retrieve(List<String> icaos, int concurrency) {
    return Flux.fromIterable(icaos)
      .buffer(CHUNK_SIZE)
      .flatMap(this::retrieveChunk, Math.max(1, concurrency));
  }

  @Override
  public Mono<RetrievalResult> retry(String icao) {
    return retrieve(icao);
  }

  @Override
  public Flux<RetrievalResult> retry(List<String> icaos, int concurrency) {
    return retrieve(icaos, concurrency);
  }

  private Mono<List<AwcTafResponse>> fetch(List<String> icaos) {
    return webClient.get()
      .uri(uri(icaos))
      .header("Accept", ACCEPT_HEADER)
      .retrieve()
      .bodyToMono(AWC_TAF_RESPONSE_LIST)
      .timeout(RESPONSE_TIMEOUT);
  }

  private Flux<RetrievalResult> retrieveChunk(List<String> icaos) {
    return fetch(icaos)
      .flatMapMany(responses -> {
        Map<String, AwcTafResponse> responseByIcao = responses.stream()
          .collect(Collectors.toMap(
            response -> response.icaoId().toUpperCase(Locale.ROOT),
            response -> response,
            (existingResponse, duplicateResponse) -> existingResponse
          ));

        return Flux.fromIterable(icaos)
          .map(icao -> {
            AwcTafResponse response = responseByIcao.get(icao.toUpperCase(Locale.ROOT));

            if (response == null) {
              return RetrievalResult.failed(icao, RetrievalFailureReason.EMPTY_RESPONSE, "AWC returned no TAF report.");
            }

            String reportText = response.rawTAF();
            if (reportText == null || reportText.isBlank()) {
              return RetrievalResult.failed(icao, RetrievalFailureReason.EMPTY_RESPONSE, "AWC returned a blank rawTAF.");
            }

            return RetrievalResult.succeeded(icao, reportText);
          });
      })
      .onErrorResume(error -> {
        RetrievalFailureReason failureReason = failureReason(error);
        String failureDetail = error.getMessage();

        return Flux.fromIterable(icaos)
          .map(icao -> RetrievalResult.failed(icao, failureReason, failureDetail));
      });
  }

  private static URI uri(List<String> icaos) {
    String joinedIcaos = String.join(",", icaos);
    return URI.create(EXTERNAL_API_URL + "?ids=" + joinedIcaos + "&format=" + RESPONSE_FORMAT);
  }

  private static RetrievalFailureReason failureReason(Throwable error) {
    if (error instanceof TimeoutException) {
      return RetrievalFailureReason.REQUEST_TIMEOUT;
    }
    if (error instanceof WebClientRequestException) {
      return RetrievalFailureReason.CONNECTION_FAILED;
    }
    if (error instanceof WebClientResponseException responseException) {
      if (responseException.getStatusCode().is4xxClientError()) {
        return RetrievalFailureReason.HTTP_CLIENT_ERROR;
      }
      if (responseException.getStatusCode().is5xxServerError()) {
        return RetrievalFailureReason.HTTP_SERVER_ERROR;
      }
    }
    return RetrievalFailureReason.UNKNOWN;
  }

  private record AwcTafResponse(String icaoId, String rawTAF) {
  }

}
