package com.axelor.apps.pbproject.service;

import com.axelor.apps.project.db.ProjectTask;
import com.axelor.meta.db.MetaFile;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public interface PBPProjectTaskService {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd_H_m_s");
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    MetaFile generateReportByPeriod(LocalDate startDate, LocalDate endDate) throws Exception;

    MetaFile generateReportForCurrentMonth() throws Exception;

    MetaFile generateExcelForTasks(List<ProjectTask> tasks) throws Exception;

}
