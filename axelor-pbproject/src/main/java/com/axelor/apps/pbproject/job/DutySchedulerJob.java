package com.axelor.apps.pbproject.job;

import com.axelor.apps.base.job.ThreadedBaseJob;
import com.axelor.apps.pbproject.service.DutyService;
import com.axelor.inject.Beans;
import groovy.util.logging.Slf4j;
import org.quartz.JobExecutionContext;

import java.time.LocalDate;

public class DutySchedulerJob extends ThreadedBaseJob {

    @Override
    public void executeInThread(JobExecutionContext context) {
        Beans.get(DutyService.class).createDutyForCurrentWeek();
    }
}
