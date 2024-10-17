package com.axelor.apps.pbproject.service.impl;

import com.axelor.apps.pbproject.db.Duty;
import com.axelor.apps.pbproject.db.WeeklyDataDto;
import com.axelor.apps.pbproject.db.repo.DutyRepository;
import com.axelor.apps.pbproject.service.WeeklyService;
import com.axelor.auth.db.User;
import com.google.inject.Inject;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class WeeklyServiceImpl implements WeeklyService {
    private final DutyServiceImpl dutyService;
    private final DutyRepository dutyRepository;

    @Inject
    public WeeklyServiceImpl(DutyServiceImpl dutyService, DutyRepository dutyRepository) {
        this.dutyService = dutyService;
        this.dutyRepository = dutyRepository;
    }

    @Override
    public WeeklyDataDto getWeekData() {
        WeeklyDataDto weeklyDataDto = new WeeklyDataDto();
        LocalDate startWeek = dutyService.getStartOfWeek();
        LocalDate endWeek = dutyService.getEndOfWeek(startWeek);

        weeklyDataDto.setStartWeekDate(startWeek);
        weeklyDataDto.setEndWeekDate(endWeek);

        List<User> users = getUsersForDutyThisWeek();
        weeklyDataDto.setFirstDutyUserFullName(users.get(0).getFullName());
        weeklyDataDto.setSecondDutyUserFullName(users.get(1).getFullName());

        return weeklyDataDto;
    }

    public List<User> getUsersForDutyThisWeek() {
        LocalDate currentStartDate = dutyService.getStartOfWeek();

        Duty dutyForThisWeek = dutyRepository.all()
                .filter("self.dateStart = :dateStartCurrent")
                .bind("dateStartCurrent", currentStartDate)
                .fetchOne();

        return new ArrayList<>(dutyForThisWeek.getUsers());
    }
}
