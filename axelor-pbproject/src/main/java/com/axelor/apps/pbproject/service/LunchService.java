package com.axelor.apps.pbproject.service;

import com.axelor.apps.pbproject.db.DishMenu;
import com.axelor.apps.pbproject.db.Lunch;

import java.util.List;

public interface LunchService {
    List<Lunch> getLunchYesterday();
    String getLinks(DishMenu dishMenu);
    String getLunch();

    void orderLunch(Long lunch, String portion);
}
