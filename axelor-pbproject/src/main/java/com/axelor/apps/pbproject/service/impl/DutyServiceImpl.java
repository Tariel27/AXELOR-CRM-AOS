package com.axelor.apps.pbproject.service.impl;

import com.axelor.apps.pbproject.db.Duty;
import com.axelor.apps.pbproject.db.repo.DutyRepository;
import com.axelor.apps.pbproject.service.DutyService;
import com.axelor.auth.db.User;
import com.axelor.auth.db.repo.UserRepository;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

public class DutyServiceImpl implements DutyService {
    private final DutyRepository dutyRepository;
    private final UserRepository userRepository;


    @Inject
    public DutyServiceImpl(DutyRepository dutyRepository, UserRepository userRepository) {
        this.dutyRepository = dutyRepository;
        this.userRepository = userRepository;
    }


    @Override
    public Duty getCurrentDuty(LocalDate date) {
        Duty currentDuty = dutyRepository.all()
                .filter("self.dateStart <= :date AND self.dateEnd >= :date")
                .bind("date", date)
                .fetchOne();

        if (currentDuty == null) {
            currentDuty = createDutyForCurrentWeek(date);
        }
        System.out.println("currentDuty = " + currentDuty);
        return currentDuty;
    }

    @Transactional
    private Duty createDutyForCurrentWeek(LocalDate date) {
        LocalDate startOfWeek = date.with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1);
        LocalDate endOfWeek = date.with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 7);

        List<User> availableUsers = userRepository.all()
                .filter("self.isActiveDuty = true AND self.alreadyInDuty = false")
                .fetch();

        if (availableUsers.size() > 2) {
            availableUsers = availableUsers.subList(0, 2);
        }

        Duty newDuty = new Duty();
        newDuty.setDateStart(startOfWeek);
        newDuty.setDateEnd(endOfWeek);
        newDuty.setUsers(new HashSet<>(availableUsers));
        availableUsers.forEach(user -> user.setAlreadyInDuty(true));

        return dutyRepository.save(newDuty);

    }
}
