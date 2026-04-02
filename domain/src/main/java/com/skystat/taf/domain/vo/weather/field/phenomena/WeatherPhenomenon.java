package com.skystat.taf.domain.vo.weather.field.phenomena;

import com.skystat.taf.domain.vo.weather.field.WeatherCode;

import static com.skystat.taf.domain.vo.weather.field.phenomena.WeatherPhenomenonGroup.*;

public enum WeatherPhenomenon implements WeatherCode {

  DRIZZLE("DZ","Drizzle", PRECIPITATION),
  RAIN("RA","Rain", PRECIPITATION),
  SNOW("SN","Snow", PRECIPITATION),
  SNOW_GRAINS("SG","Snow Grains", PRECIPITATION),
  ICE_CRYSTALS("IC","Ice Crystals", PRECIPITATION),
  ICE_PELLETS("PL","Ice Pellets", PRECIPITATION),
  HAIL("GR","Hail", OBSCURATION),
  SMALL_HAIL_SNOW_PELLETS("GS","Small Hail/Snow Pellets", OBSCURATION),
  UNKNOWN_PRECIPITATION("UP","Unknown Precipitation", OBSCURATION),
  MIST("BR","Mist", OBSCURATION),
  FOG("FG","Fog", OBSCURATION),
  SMOKE("FU","Smoke", OBSCURATION),
  VOLCANIC_ASH("VA","Volcanic Ash", OBSCURATION),
  WIDESPREAD_DUST("DU","Widespread Dust", OBSCURATION),
  SAND("SA","Sand", OBSCURATION),
  HAZE("HZ","Haze", OBSCURATION),
  SPRAY("PY","Spray", OBSCURATION),
  DUST_SAND_WHIRLS("PO","Dust/Sand Whirls", SPECIAL),
  SQUALL("SQ","Squall", SPECIAL),
  FUNNEL_CLOUD_TORNADO("FC","Funnel Cloud / Tornado", SPECIAL),
  SANDSTORM("SS","Sandstorm", SPECIAL),
  DUSTSTORM("DS","Duststorm", SPECIAL),
  WIND_SHEAR("WS","Wind Shear", SPECIAL),
  SNOW_RAIN("SNRA", "Snow Rain", PRECIPITATION),
  RAIN_SNOW("RASN", "Rain Snow", PRECIPITATION),
  NIL_SIGNIFICANT_WEATHER("NSW","Nil Significant Weather", SIGNIFICANT);

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
