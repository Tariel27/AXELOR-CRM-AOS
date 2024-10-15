package com.axelor.apps.pbproject.service;


import com.axelor.auth.db.User;

import java.util.List;

public interface DutyService {
    void createDutyForCurrentWeek();
    void updateDutyUsers();
    void updateDutyUsersFlags(List<User> userSet, Boolean activeForDuty, Boolean alreadyInDuty);

}
