package com.axelor.apps.pbproject.job;

import com.axelor.apps.base.job.ThreadedBaseJob;
import com.axelor.apps.pbproject.service.LunchService;
import com.axelor.inject.Beans;
import org.quartz.JobExecutionContext;

public class LunchSchedulerJob extends ThreadedBaseJob {

    @Override
    public void executeInThread(JobExecutionContext context) {
        Beans.get(LunchService.class).autoOrder();
    }
}
