package com.axelor.apps.pbproject.job;

import com.axelor.apps.base.job.ThreadedBaseJob;
import com.axelor.apps.pbproject.service.HolidayService;
import com.axelor.inject.Beans;
import org.quartz.JobExecutionContext;

public class HolidaySchedulerJob extends ThreadedBaseJob {

    @Override
    public void executeInThread(JobExecutionContext context) {
        Beans.get(HolidayService.class).fetchHolidaysFromApi();
        Beans.get(HolidayService.class).addHolidaysToCalendar();
    }
}
