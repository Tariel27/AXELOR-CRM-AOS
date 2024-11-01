package com.axelor.apps.pbproject.service;

import com.axelor.apps.project.db.ProjectTask;
import com.axelor.auth.db.User;

import java.util.List;
import java.util.Map;

public interface AwardsService {
    void recognizeAward(ProjectTask projectTask);

    List<Map<String, Object>> getUserAwards(User user);
}
