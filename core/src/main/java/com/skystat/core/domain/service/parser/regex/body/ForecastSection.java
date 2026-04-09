package com.skystat.core.domain.service.parser.regex.body;

import com.skystat.core.domain.vo.taf.ChangeIndicator;

record ForecastSection(ChangeIndicator indicator, String sectionRaw) {
}
