package com.skystat.taf.domain.vo.weather.field;

import com.skystat.taf.domain.vo.weather.unit.LengthUnit;

import java.util.Objects;

public record Visibility(double value, LengthUnit unit) {

	public static final double MAX_VISIBILITY_METER = 10000.0;
	public static final double MAX_VISIBILITY_MILE = LengthUnit.SM.fromStandardUnitValue(10000.0);

	public Visibility {
		Objects.requireNonNull(unit, "Visibility unit cannot be null.");

		if (value < 0) {
			throw new IllegalArgumentException("Visibility value cannot be negative.");
		}
	}

	public static Visibility p6sm() {
		return new Visibility(MAX_VISIBILITY_MILE, LengthUnit.SM);
	}

	public static Visibility cavok() {
		return new Visibility(MAX_VISIBILITY_METER, LengthUnit.METER);
	}

}
