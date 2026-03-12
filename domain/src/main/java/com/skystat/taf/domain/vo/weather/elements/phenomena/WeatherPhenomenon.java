package com.skystat.taf.domain.vo.weather.elements.phenomena;

import com.skystat.taf.domain.vo.weather.elements.WeatherCode;

import static com.skystat.taf.domain.vo.weather.elements.phenomena.WeatherPhenomenonGroup.*;

public enum WeatherPhenomenon implements WeatherCode {

  DZ("DZ","Drizzle", PRECIPITATION),
  RA("RA","Rain", PRECIPITATION),
  SN("SN","Snow", PRECIPITATION),
  SG("SG","Snow Grains", PRECIPITATION),
  IC("IC","Ice Crystals", PRECIPITATION),
  PL("PL","Ice Pellets", PRECIPITATION),
  GR("GR","Hail", OBSCURATION),
  GS("GS","Small Hail/Snow Pellets", OBSCURATION),
  UP("UP","Unknown Precipitation", OBSCURATION),
  BR("BR","Mist", OBSCURATION),
  FG("FG","Fog", OBSCURATION),
  FU("FU","Smoke", OBSCURATION),
  VA("VA","Volcanic Ash", OBSCURATION),
  DU("DU","Widespread Dust", OBSCURATION),
  SA("SA","Sand", OBSCURATION),
  HZ("HZ","Haze", OBSCURATION),
  PY("PY","Spray", OBSCURATION),
  PO("PO","Dust/Sand Whirls", SPECIAL),
  SQ("SQ","Squall", SPECIAL),
  FC("FC","Funnel Cloud / Tornado", SPECIAL),
  SS("SS","Sandstorm", SPECIAL),
  DS("DS","Duststorm", SPECIAL),
  WS("WS","Wind Shear", SPECIAL),
  SNRA("SNRA", "Snow Rain", PRECIPITATION),
  RASN("RASN", "Rain Snow", PRECIPITATION),
  NSW("NSW","Nil Significant Weather", SIGNIFICANT);

  private final String symbol;
  private final String description;
  private final WeatherPhenomenonGroup group;

  WeatherPhenomenon(String symbol, String description, WeatherPhenomenonGroup group) {
    this.symbol = symbol;
    this.description = description;
    this.group = group;
  }

  @Override
  public String symbol() {
    return symbol;
  }

  @Override
  public String description() {
    return description;
  }

  public WeatherPhenomenonGroup group() {
    return group;
  }

}
