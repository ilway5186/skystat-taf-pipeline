package com.skystat.core.domain.vo.weather.field.wind;

import java.util.Objects;

public record WindDirection(WindDirectionType type, Double degree) {

	public WindDirection {
		Objects.requireNonNull(type, "WindDirectionType cannot be null.");

		if (type == WindDirectionType.FIXED && degree == null) {
			throw new IllegalArgumentException("Fixed wind direction requires a specific degree");
		}

		if (type == WindDirectionType.VARIABLE && degree != null) {
			throw new IllegalArgumentException("Variable Wind cannot have a degree");
		}

		if (degree != null && (degree < 0d || degree > 360d)) {
			throw new IllegalArgumentException("Degree must be between 0 and 360");
		}
	}

	public static WindDirection fixed(Double degree) {
		return new WindDirection(WindDirectionType.FIXED, degree);
	}

	public static WindDirection variable() {
		return new WindDirection(WindDirectionType.VARIABLE, null);
	}

}
