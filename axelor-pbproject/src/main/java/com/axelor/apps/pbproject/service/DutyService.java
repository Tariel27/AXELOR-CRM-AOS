package com.axelor.apps.pbproject.service;

import com.axelor.apps.pbproject.db.Duty;

import java.time.LocalDate;

public interface DutyService {
    Duty getCurrentDuty(LocalDate date);

}
