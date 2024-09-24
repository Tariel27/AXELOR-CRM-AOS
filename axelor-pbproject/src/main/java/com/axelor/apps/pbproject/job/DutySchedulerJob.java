package com.axelor.apps.pbproject.job;

import com.axelor.apps.base.job.ThreadedBaseJob;
import com.axelor.apps.base.job.ThreadedBaseJob;
import com.axelor.apps.pbproject.service.DutyService;
import com.google.inject.Inject;
import groovy.util.logging.Slf4j;
import org.quartz.JobExecutionContext;

import java.time.LocalDate;

@Slf4j
public class DutySchedulerJob extends ThreadedBaseJob {

    private final DutyService dutyService;

    @Inject
    public DutySchedulerJob(DutyService dutyService) {
        this.dutyService = dutyService;
    }

    @Override
    public void executeInThread(JobExecutionContext context) {
        try {
            LocalDate today = LocalDate.now();

            if (today.getDayOfWeek().getValue() == 1) {
                dutyService.getCurrentDuty(today);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
