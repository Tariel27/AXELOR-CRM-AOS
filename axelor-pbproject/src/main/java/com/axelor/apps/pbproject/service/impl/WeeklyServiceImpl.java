package com.axelor.apps.pbproject.service.impl;
import com.axelor.apps.pbproject.db.repo.DutyRepository;
import com.axelor.apps.pbproject.service.WeeklyService;
import com.google.inject.Inject;

import java.time.LocalDate;
import java.util.Map;

public class WeeklyServiceImpl implements WeeklyService {
    private final DutyServiceImpl dutyService;
    private final DutyRepository dutyRepository;

    @Inject
    public WeeklyServiceImpl(DutyServiceImpl dutyService, DutyRepository dutyRepository) {
        this.dutyService = dutyService;
        this.dutyRepository = dutyRepository;
    }

    @Override
    public String getDates() {
        String startWeek = String.valueOf(dutyService.getStartOfWeek());
        String endWeek = String.valueOf(dutyService.getEndOfWeek(LocalDate.parse(startWeek)));
        return startWeek + " - " + endWeek;
    }

    @Override
    public Map<String, Object> getWeeklyData() {
        Map<String, Object> weeklyData = dutyService.getWeekDataProvider();
        return weeklyData;
    }

}
