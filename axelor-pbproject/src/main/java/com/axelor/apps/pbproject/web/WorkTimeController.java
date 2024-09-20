package com.axelor.apps.pbproject.web;

import com.axelor.apps.pbproject.db.UserHoursManagement;
import com.axelor.apps.pbproject.db.WorkHoursDto;
import com.axelor.apps.pbproject.service.WorkTimeService;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.Inject;

import java.util.List;

public class WorkTimeController {

    private final WorkTimeService workTimeService;

    @Inject
    public WorkTimeController(WorkTimeService workTimeService) {
        this.workTimeService = workTimeService;
    }
    public void calculateWorkHours(ActionRequest actionRequest, ActionResponse actionResponse){

        UserHoursManagement userHoursManagement = actionRequest.getContext().asType(UserHoursManagement.class);

        List<WorkHoursDto> workHours = workTimeService.getWorkHours(userHoursManagement);

        actionResponse.setValue("workHours", workHours);
    }
}
