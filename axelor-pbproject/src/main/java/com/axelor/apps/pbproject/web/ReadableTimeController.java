package com.axelor.apps.pbproject.web;

import com.axelor.apps.pbproject.service.ReadableTimeService;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.Inject;

import java.math.BigDecimal;

public class ReadableTimeController {
    private final ReadableTimeService readableTimeService;

    @Inject
    public ReadableTimeController(ReadableTimeService readableTimeService) {
        this.readableTimeService = readableTimeService;
    }

    public void convertToReadable(ActionRequest actionRequest, ActionResponse actionResponse){
        BigDecimal spentTime = (BigDecimal) actionRequest.getContext().get("spentTime");

        String readableTime = readableTimeService.calculateReadableTime(spentTime);

        actionResponse.setValue("readableSpentTime", readableTime);
    }
}
