package com.axelor.apps.pbproject.web;

import com.axelor.apps.pbproject.db.WeeklyDataDto;
import com.axelor.apps.pbproject.service.WeeklyService;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.Inject;

import java.util.Objects;

public class WeeklyDataController {
    private final WeeklyService weeklyService;

    @Inject
    public WeeklyDataController(WeeklyService weeklyService) {
        this.weeklyService = weeklyService;
    }

    public void getWeeklyData(ActionRequest actionRequest, ActionResponse actionResponse){
        WeeklyDataDto weeklyDataDto = weeklyService.getWeekData();
        if (Objects.isNull(weeklyDataDto)){
            actionResponse.setError("Weekly data is null!");
            return;
        }

        actionResponse.setData(weeklyDataDto);
    }
}
