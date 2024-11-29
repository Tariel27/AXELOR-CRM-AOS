package com.axelor.apps.pbproject.service.impl;

import com.axelor.apps.pbproject.db.Lunch;
import com.axelor.apps.pbproject.db.repo.LunchRepository;
import com.axelor.apps.pbproject.service.LunchService;
import com.google.inject.Inject;

import java.time.LocalDate;
import java.util.List;

public class LunchServiceImpl implements LunchService {
    private final LunchRepository lunchRepository;

    @Inject
    public LunchServiceImpl(LunchRepository lunchRepository) {
        this.lunchRepository = lunchRepository;
    }

    @Override
    public List<Lunch> getLunchYesterday() {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        return lunchRepository.all()
                .filter("self.dateCreate = :yesterday")
                .bind("yesterday", yesterday)
                .fetch();
    }
}
