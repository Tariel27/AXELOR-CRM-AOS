package com.axelor.apps.pbproject.web;

import com.axelor.apps.pbproject.service.EmailService;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.Inject;

import java.util.Objects;

public class WorkLeaveController {
    private final EmailService emailService;

    @Inject
    public WorkLeaveController(EmailService emailService) {
        this.emailService = emailService;
    }


    public void sendEmail(ActionRequest actionRequest, ActionResponse actionResponse){
        Long leaveRequestId = (Long) actionRequest.getContext().get("id");
        if (Objects.isNull(leaveRequestId)) actionResponse.setError("Id of request is null");
        try {
            emailService.sendEmail(leaveRequestId);
        } catch (RuntimeException e){
            actionResponse.setError(e.getMessage());
        }
    }
}
