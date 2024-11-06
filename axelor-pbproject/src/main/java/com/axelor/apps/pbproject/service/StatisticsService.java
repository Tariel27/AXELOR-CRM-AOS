package com.axelor.apps.pbproject.service;

import java.util.Map;

public interface StatisticsService {

    Map<String, Object> getUserAwgStat(Long userId);

    Map<String, Object> getStatOfTasks(Long id);

    Map<String, Object> getKPDTasks(Long userId);
    Map<String, Object> getLastActivityStat(Long userId);
}
