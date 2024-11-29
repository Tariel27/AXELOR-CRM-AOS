package com.axelor.apps.pbproject.service;

import com.axelor.apps.pbproject.db.Lunch;

import java.util.List;

public interface LunchService {
    List<Lunch> getLunchYesterday();
}
