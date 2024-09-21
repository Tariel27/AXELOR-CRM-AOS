package com.axelor.apps.pbproject.service.impl;

import com.axelor.apps.pbproject.service.ReadableTimeService;

import java.math.BigDecimal;

public class ReadableTimeServiceImpl implements ReadableTimeService {
    @Override
    public String calculateReadableTime(BigDecimal totalWorkedHours) {
        if (totalWorkedHours == null) {
            return "00:00";
        }

        int totalMinutes = totalWorkedHours.multiply(BigDecimal.valueOf(60)).intValue();
        int hours = totalMinutes / 60;
        int minutes = totalMinutes % 60;

        return String.format("%02d:%02d", hours, minutes);
    }
}
