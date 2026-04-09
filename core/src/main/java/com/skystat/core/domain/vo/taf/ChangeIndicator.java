package com.skystat.core.domain.vo.taf;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public enum ChangeIndicator {
  HEADER(ReferencePolicy.REPLACE),
  BECMG(ReferencePolicy.MERGE),
  FM(ReferencePolicy.REPLACE),
  TEMPO(ReferencePolicy.NONE),
  INTER(ReferencePolicy.NONE),
  PROB30(ReferencePolicy.NONE),
  PROB40(ReferencePolicy.NONE),
  PROB30_TEMPO(ReferencePolicy.NONE),
  PROB40_TEMPO(ReferencePolicy.NONE),
  NONE(ReferencePolicy.NONE);

  private final ReferencePolicy referencePolicy;

  public boolean referenceable() {
    return referencePolicy != ReferencePolicy.NONE;
  }

}
