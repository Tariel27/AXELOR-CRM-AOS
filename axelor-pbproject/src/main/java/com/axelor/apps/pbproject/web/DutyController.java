package com.axelor.apps.pbproject.web;

import com.axelor.apps.pbproject.db.Duty;
import com.axelor.apps.pbproject.service.DutyService;
import com.axelor.auth.db.User;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.Inject;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public class DutyController {
    private final DutyService dutyService;

    @Inject
    public DutyController(DutyService dutyService) {
        this.dutyService = dutyService;
    }

    public void getDutyUsers(ActionRequest actionRequest,ActionResponse actionResponse) {
        LocalDate today = LocalDate.now();

        Duty currentDuty = dutyService.getCurrentDuty(today);

        if (currentDuty == null) {
            actionResponse.setError("No duty scheduled for this week.");
            return;
        }

        Set<User> dutyUsers = currentDuty.getUsers();


        actionResponse.setValue("users", dutyUsers);
    }
}
