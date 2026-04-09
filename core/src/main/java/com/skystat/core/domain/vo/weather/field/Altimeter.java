package com.skystat.core.domain.vo.weather.field;

import com.skystat.core.domain.vo.weather.unit.PressureUnit;

import java.util.Objects;

public record Altimeter(double value, PressureUnit unit) {

	public Altimeter {
		Objects.requireNonNull(unit, "Altimeter unit cannot be null.");

		if (value < 0) {
			throw new IllegalArgumentException("Altimeter value cannot be negative.");
		}
	}

}
