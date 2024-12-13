package com.axelor.apps.pbproject.job;

import com.axelor.apps.base.job.ThreadedBaseJob;
import com.axelor.apps.pbproject.service.DutyService;
import com.axelor.inject.Beans;
import org.quartz.JobExecutionContext;

public class DutySchedulerJob extends ThreadedBaseJob {

    @Override
    public void executeInThread(JobExecutionContext context) {
        Beans.get(DutyService.class).closeWeek();
        Beans.get(DutyService.class).openWeek();
    }
}
