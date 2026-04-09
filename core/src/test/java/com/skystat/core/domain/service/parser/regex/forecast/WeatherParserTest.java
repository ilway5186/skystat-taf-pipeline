package com.skystat.core.domain.service.parser.regex.forecast;

import com.skystat.core.domain.vo.weather.field.phenomena.Weather;
import com.skystat.core.domain.vo.weather.field.phenomena.WeatherDescriptor;
import com.skystat.core.domain.vo.weather.field.phenomena.WeatherPhenomenon;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WeatherParserTest {

  private final WeatherParser parser = new WeatherParser();

  @Test
  void 날씨코드_목록을_파싱한다() {
    List<Weather> weathers = parser.parse("4000 -RA FG BR");

    assertEquals(3, weathers.size());
    assertEquals(List.of(WeatherPhenomenon.RAIN), weathers.get(0).phenomena());
    assertEquals(List.of(WeatherPhenomenon.FOG), weathers.get(1).phenomena());
    assertEquals(List.of(WeatherPhenomenon.MIST), weathers.get(2).phenomena());
  }

  @Test
  void descriptor를_포함한_날씨코드를_파싱한다() {
    List<Weather> weathers = parser.parse("TSRA");

    assertEquals(1, weathers.size());
    assertEquals(List.of(WeatherDescriptor.THUNDERSTORM), weathers.getFirst().descriptors());
    assertEquals(List.of(WeatherPhenomenon.RAIN), weathers.getFirst().phenomena());
  }

  @Test
  void 날씨코드가_없으면_빈리스트다() {
    assertEquals(List.of(), parser.parse("09010KT 9999 BKN010"));
  }
}
