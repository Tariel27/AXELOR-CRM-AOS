package com.axelor.apps.pbproject.web;

import com.axelor.apps.pbproject.db.UserHoursManagement;
import com.axelor.apps.pbproject.db.WorkHoursDto;
import com.axelor.apps.pbproject.service.WorkTimeService;
import com.axelor.apps.project.db.Project;
import com.axelor.apps.project.db.ProjectTask;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.Inject;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class WorkTimeController {

    private final WorkTimeService workTimeService;

    @Inject
    public WorkTimeController(WorkTimeService workTimeService) {
        this.workTimeService = workTimeService;
    }
    public void calculateWorkHours(ActionRequest actionRequest, ActionResponse actionResponse){

        UserHoursManagement userHoursManagement = actionRequest.getContext().asType(UserHoursManagement.class);

        List<WorkHoursDto> lines = workTimeService.getWorkHours(userHoursManagement);

        actionResponse.setValue("lines", lines);
    }

    public void validateStartEndTimeTask(ActionRequest actionRequest, ActionResponse actionResponse){
        LocalDateTime startDateT = (LocalDateTime) actionRequest.getContext().get("startDateTime");
        LocalDateTime endDateT = (LocalDateTime) actionRequest.getContext().get("endDateTime");
        if (Objects.isNull(startDateT) || Objects.isNull(endDateT)){
            return;
        }

        if (startDateT.isAfter(endDateT)){
            actionResponse.setError("The start date cannot be later than the end date");
        }
    }
}
