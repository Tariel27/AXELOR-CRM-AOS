package com.axelor.apps.pbproject.service;

import com.axelor.apps.pbproject.db.UserHoursManagement;
import com.axelor.apps.pbproject.db.WorkHoursDto;

import java.util.List;

public interface WorkTimeService {

    List<WorkHoursDto> getWorkHours(UserHoursManagement userHoursManagement);
}
