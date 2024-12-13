package com.axelor.apps.pbproject.web;

import com.axelor.apps.pbproject.service.HolidayService;
import com.axelor.meta.CallMethod;
import com.google.inject.Inject;


public class EventController {

    private final HolidayService holidayService;

    @Inject
    public EventController(HolidayService holidayService) {
        this.holidayService = holidayService;
    }

    @CallMethod
    public String getEventSizeByMonth(){
        return holidayService.getEventSizeByMonth();
    }
}
