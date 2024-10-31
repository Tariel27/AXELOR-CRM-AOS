package com.axelor.apps.pbproject.service;


import java.util.Map;

public interface WeeklyService {
    String getDates();
    Map<String, Object> getWeeklyData();
}
