package com.axelor.apps.pbproject.web;

import com.axelor.apps.pbproject.service.HolidayService;
import com.google.inject.Inject;


public class EventController {

    private final HolidayService holidayService;

    @Inject
    public EventController(HolidayService holidayService) {
        this.holidayService = holidayService;
    }

    public String getEventSizeByMonth(){
        return holidayService.getEventSizeByMonth();
    }
}
