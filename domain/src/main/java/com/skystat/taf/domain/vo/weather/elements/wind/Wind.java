package com.skystat.taf.domain.vo.weather.elements.wind;

import com.skystat.taf.domain.vo.weather.unit.SpeedUnit;

import java.util.Objects;

public record Wind(
	WindDirection direction,
	Double speed,
	Double gust,
	SpeedUnit unit,
	Double speedStandard,
	Double gustStandard
) {

	public static final double WIND_STANDARD_LIMIT = 200;

	public Wind {
		Objects.requireNonNull(direction, "WindDirection cannot be null.");
		Objects.requireNonNull(unit, "SpeedUnit cannot be null.");

		if (speed != null && speed < 0) {
			throw new IllegalArgumentException("Speed cannot be negative.");
		}

		if (gust != null && gust < 0) {
			throw new IllegalArgumentException("Gusts cannot be negative.");
		}

		if ((speed != null && speedStandard == null) || (gust != null && gustStandard == null)) {
			throw new IllegalArgumentException("Standard values must be present if raw values exist.");
		}

		double sKt = speedStandard != null ? speedStandard : 0.0;
		double gKt = gustStandard != null ? gustStandard : 0.0;
		if (sKt > WIND_STANDARD_LIMIT || gKt > WIND_STANDARD_LIMIT) {
			throw new IllegalArgumentException(String.format(
				"Wind speed/gust can't exceed %sKT. speed: %s, gust: %s, unit: %s",
				WIND_STANDARD_LIMIT, speed, gust, unit
			));
		}
	}

	public Double peakSpeed() {
		if (speed == null && gust == null) return null;
		if (speed == null) return gust;
		if (gust == null) return speed;
		return Math.max(speed, gust);
	}

	public Double peakSpeedKt() {
		if (speedStandard == null && gustStandard == null) return null;
		if (speedStandard == null) return gustStandard;
		if (gustStandard == null) return speedStandard;
		return Math.max(speedStandard, gustStandard);
	}

}
