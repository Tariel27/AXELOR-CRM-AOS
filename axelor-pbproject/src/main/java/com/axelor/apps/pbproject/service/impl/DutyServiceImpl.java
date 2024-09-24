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
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class DutyServiceImpl implements DutyService {
    private final DutyRepository dutyRepository;
    private final UserRepository userRepository;


    @Inject
    public DutyServiceImpl(DutyRepository dutyRepository, UserRepository userRepository) {
        this.dutyRepository = dutyRepository;
        this.userRepository = userRepository;
    }


    @Override
    @Transactional
    public Duty getCurrentDuty(LocalDate date) {
        Duty currentDuty = dutyRepository.all()
                .filter("self.dateStart <= :date AND self.dateEnd >= :date")
                .bind("date", date)
                .fetchOne();

        if (currentDuty == null) {
            currentDuty = createDutyForCurrentWeek(date);
        }
        return currentDuty;
    }

    @Transactional
    private Duty createDutyForCurrentWeek(LocalDate date) {
        LocalDate startOfWeek = date.with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1);
        LocalDate endOfWeek = date.with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 7);

        //сюда огика сброса если все люди отдежурили
        List<User> activeUsers = userRepository.all()
                .filter("self.isActiveDuty = true")
                .fetch();
        resetDutyFlagsIfAllInDuty(activeUsers);


        List<User> availableUsers = userRepository.all()
                .filter("self.isActiveDuty = true AND self.alreadyInDuty = false")
                .fetch(2);

        availableUsers.forEach(user -> {
            user.setAlreadyInDuty(true);
            userRepository.save(user);
        });

        Duty newDuty = new Duty();
        newDuty.setDateStart(startOfWeek);
        newDuty.setDateEnd(endOfWeek);
        newDuty.setUsers(Set.copyOf(availableUsers));

        dutyRepository.save(newDuty);

        return newDuty;
    }

    @Transactional
    private void resetDutyFlagsIfAllInDuty(List<User> activeUsers) {
        if (activeUsers.stream().allMatch(User::getAlreadyInDuty)) {
            activeUsers.forEach(user -> {
                user.setAlreadyInDuty(false);
                userRepository.save(user);
            });
        }
    }

}
