package com.axelor.apps.pbproject.service;

import java.math.BigDecimal;

public interface ReadableTimeService {
    String calculateReadableTime(BigDecimal totalWorkedHours);
}
