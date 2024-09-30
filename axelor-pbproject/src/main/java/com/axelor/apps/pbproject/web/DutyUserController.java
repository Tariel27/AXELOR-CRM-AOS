package com.axelor.apps.pbproject.web;

import com.axelor.apps.pbproject.service.DutyService;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.Inject;

public class DutyUserController {
    private final DutyService dutyService;

    @Inject
    public DutyUserController(DutyService dutyService) {
        this.dutyService = dutyService;
    }

    public void updateDutyUsers(ActionRequest actionRequest, ActionResponse actionResponse){
        dutyService.updateDutyUsers();
    }
}
