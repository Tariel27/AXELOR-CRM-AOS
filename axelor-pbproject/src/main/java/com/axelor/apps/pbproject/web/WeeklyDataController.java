package com.axelor.apps.pbproject.web;


import com.axelor.apps.pbproject.service.WeeklyService;
import com.axelor.i18n.I18n;
import com.axelor.meta.schema.actions.ActionView;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.Inject;

import java.util.Map;

public class WeeklyDataController {
    private final WeeklyService weeklyService;

    @Inject
    public WeeklyDataController(WeeklyService weeklyService) {
        this.weeklyService = weeklyService;

    }

    public void getWeeklyData(ActionRequest actionRequest, ActionResponse actionResponse){
        Map<String, Object> weeklyData = weeklyService.getWeeklyData();
        actionResponse.setData(weeklyData);
    }

    public void actionMethodForWeekly(ActionRequest actionRequest, ActionResponse actionResponse){
        String dateProp = weeklyService.getDates();
        actionResponse.setView(ActionView.define(I18n.get("Период: "+dateProp))
                .add("custom", "pbp.users-for-duty-custom-view").map());
    }
}
