package com.axelor.apps.pbproject.web;

import com.axelor.apps.pbproject.service.ReadableTimeService;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.Inject;

import java.math.BigDecimal;
import java.util.Objects;

public class ReadableTimeController {
    private final ReadableTimeService readableTimeService;

    @Inject
    public ReadableTimeController(ReadableTimeService readableTimeService) {
        this.readableTimeService = readableTimeService;
    }

    public void convertToReadable(ActionRequest actionRequest, ActionResponse actionResponse){
        BigDecimal spentTime = (BigDecimal) actionRequest.getContext().get("spentTime");
        BigDecimal planHours = (BigDecimal) actionRequest.getContext().get("planHours");

        String readableTime = readableTimeService.calculateReadableTime(spentTime);
        String planHoursCalculate = readableTimeService.calculateReadableTime(planHours);

        actionResponse.setValue("readableSpentTime", readableTime);
        actionResponse.setValue("planHoursString", planHoursCalculate);
    }
}
