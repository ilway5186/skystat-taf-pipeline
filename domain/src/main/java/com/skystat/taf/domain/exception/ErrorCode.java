package com.skystat.taf.domain.exception;

public enum ErrorCode {

  // HTTP Status 400 - Invalid 계열
  INVALID_INPUT("INVALID_INPUT", "Invalid request value."), // 요청 값이 올바르지 않습니다.
  INVALID_STATUS("INVALID_STATUS", "Cannot process the request in the current status."), // 현재 상태에서는 요청을 처리할 수 없습니다.

  // 401/403 - Auth 계열
  INVALID_TOKEN("INVALID_TOKEN", "The authentication token is invalid."), // 인증 토큰이 유효하지 않습니다.
  INVALID_CREDENTIALS("INVALID_CREDENTIALS", "Invalid username or password."), // 아이디 또는 비밀번호가 올바르지 않습니다.
  FORBIDDEN("FORBIDDEN", "You do not have permission to perform this action."), // 해당 작업을 수행할 권한이 없습니다.

  // 404/409 - Resource 계열
  RESOURCE_NOT_FOUND("NOT_FOUND", "The requested resource could not be found."), // 요청한 리소스를 찾을 수 없습니다.
  DUPLICATE_RESOURCE("DUPLICATE_RESOURCE", "The resource already exists."), // 이미 존재하는 리소스입니다.

  // 422 - Business rule 계열
  BUSINESS_RULE_VIOLATION("BUSINESS_RULE_VIOLATION", "A business rule was violated."), // 비즈니스 규칙을 위반했습니다.
  INSUFFICIENT_BALANCE("INSUFFICIENT_BALANCE", "Insufficient balance."), // 잔액이 부족합니다.

  // 502/503 - External (외부 시스템 연동관련 오류)
  EXTERNAL_SERVICE_ERROR("EXTERNAL_SERVICE_ERROR", "An error occurred while connecting to an external system."), // 외부 시스템 연동 중 오류가 발생했습니다.
  EXTERNAL_SERVICE_UNAVAILABLE("EXTERNAL_SERVICE_UNAVAILABLE", "The external system is temporarily unstable."), // 외부 시스템이 일시적으로 불안정합니다.

  // 500 - Internal (서버 오류 계열)
  INTERNAL_ERROR("INTERNAL_ERROR", "An internal server error occurred."); // 서버 오류가 발생했습니다.

  private final String code;
  private final String defaultMessage;

  ErrorCode(String code, String defaultMessage) {
    this.code = code;
    this.defaultMessage = defaultMessage;
  }

  public String code() {
    return code;
  }

  public String defaultMessage() {
    return defaultMessage;
  }

}
