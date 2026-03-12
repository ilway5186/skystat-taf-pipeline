package com.skystat.taf.domain.vo.weather.elements;

import com.skystat.taf.domain.vo.weather.unit.LengthUnit;

import java.util.Objects;

public record Visibility(double value, LengthUnit unit) {

	public Visibility {
		Objects.requireNonNull(unit, "Visibility unit cannot be null.");

		if (value < 0) {
			throw new IllegalArgumentException("Visibility value cannot be negative.");
		}
	}

	public static Visibility p6sm() {
		return new Visibility(6.0, LengthUnit.SM);
	}

	public static Visibility cavok() {
		return new Visibility(10000.0, LengthUnit.METER);
	}

}
