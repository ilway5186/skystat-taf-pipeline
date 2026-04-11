package com.skystat.taf.ingestion.retrieval.infrastructure;

import com.skystat.taf.ingestion.common.annotation.Adapter;
import com.skystat.taf.ingestion.retrieval.application.RetrievalOutputPort;
import com.skystat.taf.ingestion.retrieval.domain.Retrieval;
import com.skystat.taf.ingestion.retrieval.domain.RetrievalFailureReason;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
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
  public Mono<Retrieval> retrieve(Retrieval retrieval) {
    return Mono.defer(() -> request(List.of(retrieval))
      .map(responses -> complete(retrieval, responses))
      .onErrorResume(error -> Mono.just(failed(retrieval, failureReason(error), error.getMessage()))));
  }

  @Override
  public Flux<Retrieval> retrieve(List<Retrieval> retrievals, int concurrency) {
    return Flux.fromIterable(retrievals)
      .buffer(CHUNK_SIZE)
      .flatMap(this::retrieveChunk, Math.max(1, concurrency));
  }

  @Override
  public Mono<Retrieval> retry(Retrieval retrieval) {
    return retrieve(retrieval);
  }

  @Override
  public Flux<Retrieval> retry(List<Retrieval> retrievals, int concurrency) {
    return retrieve(retrievals, concurrency);
  }

  private static URI uri(List<String> icaos) {
    String joinedIcaos = String.join(",", icaos);
    return URI.create(EXTERNAL_API_URL + "?ids=" + joinedIcaos + "&format=" + RESPONSE_FORMAT);
  }

  private Mono<List<AwcTafResponse>> request(List<Retrieval> retrievals) {
    List<String> icaos = retrievals.stream()
      .map(Retrieval::icao)
      .toList();

    return webClient.get()
      .uri(uri(icaos))
      .header("Accept", ACCEPT_HEADER)
      .retrieve()
      .bodyToMono(AWC_TAF_RESPONSE_LIST)
      .timeout(RESPONSE_TIMEOUT);
  }

  private Flux<Retrieval> retrieveChunk(List<Retrieval> retrievals) {
    return request(retrievals)
      .flatMapMany(responses -> complete(retrievals, responses))
      .onErrorResume(error -> failed(retrievals, failureReason(error), error.getMessage()));
  }

  private static Retrieval complete(Retrieval retrieval, List<AwcTafResponse> responses) {
    return responses.stream()
      .filter(response -> retrieval.icao().equalsIgnoreCase(response.icaoId()))
      .findFirst()
      .map(response -> succeeded(retrieval, response))
      .orElseGet(() -> failed(retrieval, RetrievalFailureReason.EMPTY_RESPONSE, "AWC returned no TAF report."));
  }

  private static Flux<Retrieval> complete(List<Retrieval> retrievals, List<AwcTafResponse> responses) {
    Map<String, AwcTafResponse> responseByIcao = responses.stream()
      .collect(Collectors.toMap(AwcTafResponse::icaoId, Function.identity(), (left, right) -> left));

    return Flux.fromIterable(retrievals)
      .map(retrieval -> {
        AwcTafResponse response = responseByIcao.get(retrieval.icao());
        if (response == null) {
          return failed(retrieval, RetrievalFailureReason.EMPTY_RESPONSE, "AWC returned no TAF report.");
        }
        return succeeded(retrieval, response);
      });
  }

  private static Flux<Retrieval> failed(List<Retrieval> retrievals, RetrievalFailureReason failureReason, String failureDetail) {
    return Flux.fromIterable(retrievals)
      .map(retrieval -> failed(retrieval, failureReason, failureDetail));
  }

  private static Retrieval succeeded(Retrieval retrieval, AwcTafResponse response) {
    String reportText = response.rawTAF();
    if (reportText == null || reportText.isBlank()) {
      return failed(retrieval, RetrievalFailureReason.EMPTY_RESPONSE, "AWC returned a blank rawTAF.");
    }

    retrieval.succeed(reportText, Instant.now());
    return retrieval;
  }

  private static Retrieval failed(Retrieval retrieval, RetrievalFailureReason failureReason, String failureDetail) {
    retrieval.fail(failureReason, failureDetail, Instant.now());
    return retrieval;
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
