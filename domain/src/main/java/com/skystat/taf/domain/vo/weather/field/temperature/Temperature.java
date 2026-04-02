package com.skystat.taf.domain.vo.weather.field.temperature;

import com.skystat.taf.domain.vo.weather.unit.TemperatureUnit;

import java.util.Objects;

public record Temperature(double value, TemperatureUnit unit) {

	public Temperature {
		Objects.requireNonNull(unit, "Temperature unit cannot be null.");

		if (unit.toStandardUnitValue(value) < -273.15) {
			throw new IllegalArgumentException("Temperature value cannot be lower than absolute zero.");
		}
	}

}