package com.skystat.core.domain.vo.taf;

public enum ReferencePolicy {
  NONE,   // TEMPO, INTER, PROB는 이전 예보 내용을 유지하면서 일부 변경 사항만 변경
  MERGE,  // BECMG는 이전 예보 내용을 유지하면서 변경 사항만 바꿈
  REPLACE // HEADER, FM은 이전 예보내용을 완전히 대체
}
