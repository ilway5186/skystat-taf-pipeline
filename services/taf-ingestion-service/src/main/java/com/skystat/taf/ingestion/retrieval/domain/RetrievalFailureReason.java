package com.skystat.taf.ingestion.retrieval.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public enum RetrievalFailureReason {

  REQUEST_TIMEOUT("RETRIEVAL_REQUEST_TIMEOUT", "TAF retrieval request timed out.", true),
  CONNECTION_FAILED("RETRIEVAL_CONNECTION_FAILED", "Failed to connect to the TAF provider.", true),

  HTTP_CLIENT_ERROR("RETRIEVAL_HTTP_CLIENT_ERROR", "The TAF provider rejected the request.", false),
  HTTP_SERVER_ERROR("RETRIEVAL_HTTP_SERVER_ERROR", "The TAF provider returned a server error.", true),
  EMPTY_RESPONSE("RETRIEVAL_EMPTY_RESPONSE", "The TAF provider returned an empty response.", true),

  INVALID_RESPONSE("RETRIEVAL_INVALID_RESPONSE", "The TAF provider returned an invalid response.", false),
  UNKNOWN("RETRIEVAL_UNKNOWN_FAILURE", "An unknown TAF retrieval failure occurred.", true);

  private final String code;
  private final String message;
  private final boolean retryable;

}
