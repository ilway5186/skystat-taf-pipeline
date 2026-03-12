package com.skystat.taf.domain.vo.weather.elements.cloud;

import java.io.Serializable;
import java.util.Objects;

public record Cloud(
	String rawCode,
	CloudCoverage coverage,
	Double altitude,
	CloudType type
) implements Serializable {

	public static final double ALTITUDE_LIMIT = 100_000;

	public Cloud {
		Objects.requireNonNull(rawCode, "rawCode cannot be null.");
		Objects.requireNonNull(coverage, "coverage cannot be null.");

		// VV타입은 유일하게 altitude 값에 null을 허용함
		//  - 규정상 필수이나, 현실적으로 관측이 어려운 경우가 빈번하여 null인 경우 다수 발생
		if (coverage != CloudCoverage.VV && coverage.requiresAltitude() && altitude == null) {
			throw new IllegalArgumentException(coverage + " requires altitude (e.g., BKN030, VV002).");
		}

		if (!coverage.requiresAltitude() && altitude != null) {
			throw new IllegalArgumentException(coverage + " has no fixed altitude.");
		}

		if (altitude != null && altitude >= ALTITUDE_LIMIT) {
			throw new IllegalArgumentException("Cloud altitude can't be greater than " + ALTITUDE_LIMIT + "ft.");
		}
	}

}
