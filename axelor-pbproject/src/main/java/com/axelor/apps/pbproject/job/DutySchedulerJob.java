package com.axelor.apps.pbproject.job;

import com.axelor.apps.pbproject.service.DutyService;
import com.google.inject.Inject;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.time.LocalDate;

public class DutySchedulerJob implements Job {

    private final DutyService dutyService;

    @Inject
    public DutySchedulerJob(DutyService dutyService) {
        this.dutyService = dutyService;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        LocalDate today = LocalDate.now();

        //1-7 с понед до воскрес, надо крч настроить через UI крон шедулера или через код, спросить кэпа Тариеля
        if (today.getDayOfWeek().getValue() == 1) {
            dutyService.getCurrentDuty(today);
        }
    }
}
