package com.axelor.apps.pbproject.service;

import com.axelor.apps.pbproject.db.UserHoursManagement;
import com.axelor.apps.pbproject.db.WorkHoursDto;
import com.axelor.apps.project.db.ProjectTask;
import com.axelor.auth.db.User;

import java.math.BigDecimal;
import java.util.List;

public interface WorkTimeService {
    List<WorkHoursDto> getWorkHours(UserHoursManagement userHoursManagement);
    BigDecimal getTotalSumWorkHours(User user);
    BigDecimal getTotalEndedTasks(User user);
    List<ProjectTask> getAllTasksOfUser(User user);
}
