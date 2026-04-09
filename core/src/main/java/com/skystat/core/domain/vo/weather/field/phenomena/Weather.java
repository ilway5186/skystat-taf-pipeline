package com.skystat.core.domain.vo.weather.field.phenomena;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 전문에 포함된 각각의 날씨 그룹(Weather Group)을 담은 객체입니다.
 <p>단일 날씨 코드엔 여러 서술어와 현상이 복합적으로 포함될 수 있으므로 각각 리스트(List) 형태로 관리합니다.</p>
 <p>예시: {@code -VCTSSNPL}</p>
 <ul>
 <li>{@code List<WeatherDescriptor>}: VC, TS</li>
 <li>{@code List<WeatherPhenomenon>}: SN, PL</li>
 </ul>

 @param rawCode    원본 날씨 코드 문자열 (예: "-VCTSSNPL")
 @param intensity  기상 현상의 강도
 @param descriptors 동반되는 기상 서술어 목록 (예: VC, TS)
 @param phenomena  실제 발생하는 기상 현상 목록 (예: SN, PL)
 */
public record Weather(
	String rawCode,
	WeatherIntensity intensity,
	List<WeatherDescriptor> descriptors,
	List<WeatherPhenomenon> phenomena
) implements Serializable {

	public Weather {
		Objects.requireNonNull(rawCode, "rawCode cannot be null.");
		Objects.requireNonNull(intensity, "intensity cannot be null.");
		if (descriptors == null) {
			descriptors = List.of();
		}
		
		if (phenomena == null) {
			phenomena = List.of();
		}
	}

}