package com.axelor.apps.pbproject.service.impl;

import com.axelor.apps.pbproject.db.Duty;
import com.axelor.apps.pbproject.db.repo.DutyRepository;
import com.axelor.apps.pbproject.service.DutyService;
import com.axelor.auth.db.User;
import com.axelor.auth.db.repo.UserRepository;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

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
    public void createDutyForCurrentWeek() {
        LocalDate currentMonday = getStartOfWeek();
        LocalDate currentSunday = getEndOfWeek(currentMonday);

        List<User> activeUsersForDuty = getActiveUsers();
        resetDutyFlagsIfAllInDuty(activeUsersForDuty);

        List<User> availableUsers = getAvailableUsersForDuty();

        assignDutyToUsers(availableUsers);

        createNewDuty(currentMonday, currentSunday, availableUsers);
    }

    @Override
    @Transactional
    public void updateDutyUsers() {
        LocalDate currentMonday = getStartOfWeek();
        LocalDate currentSunday = getEndOfWeek(currentMonday);

        Duty currentDuty = getCurrentDuty(currentMonday, currentSunday);

        if (currentDuty != null) {
            Set<User> existingUsers = currentDuty.getUsers();

            List<User> newDutyUsers = getAvailableUsersForDuty(existingUsers);

            updateCurrentDutyUsers(currentDuty, newDutyUsers);

            resetAlreadyInDutyFlag(existingUsers);
        } else {
            List<User> availableUsers = getAvailableUsersForDuty();
            createNewDuty(currentMonday, currentSunday, availableUsers);
        }
    }

    private Duty getCurrentDuty(LocalDate start, LocalDate end) {
        return dutyRepository.all()
                .filter("self.dateStart >= :start AND self.dateEnd <= :end")
                .bind("start", start)
                .bind("end", end)
                .fetchOne();
    }

    private void updateCurrentDutyUsers(Duty duty, List<User> newUsers) {
        duty.setUsers(new HashSet<>(newUsers));
        dutyRepository.save(duty);
    }

    private void resetAlreadyInDutyFlag(Set<User> users) {
        users.forEach(user -> {
            user.setAlreadyInDuty(false);
            userRepository.save(user);
        });
    }


    private LocalDate getStartOfWeek() {
        return LocalDate.now().with(WeekFields.of(Locale.getDefault()).dayOfWeek(), DayOfWeek.MONDAY.getValue());
    }

    private LocalDate getEndOfWeek(LocalDate startOfWeek) {
        return startOfWeek.with(WeekFields.of(Locale.getDefault()).dayOfWeek(), DayOfWeek.SUNDAY.getValue());
    }

    private List<User> getActiveUsers() {
        return userRepository.all()
                .filter("self.isActiveDuty = true")
                .fetch();
    }

    private List<User> getAvailableUsersForDuty() {
        List<User> returnUsers = new ArrayList<>();

        List<User> availableUsers = userRepository.all()
                .filter("self.isActiveDuty = true AND self.alreadyInDuty = false")
                .fetch(2);


        if (availableUsers.size() < 2) {
            User lastUserFromAll = availableUsers.get(0);
            returnUsers.add(lastUserFromAll);

            resetAllDutyFlags();

            User secondUserForAdd = userRepository.all()
                    .filter("self.isActiveDuty = true AND self.alreadyInDuty = false AND self.id != :id")
                    .bind("id", lastUserFromAll.getId())
                    .fetchOne();
            returnUsers.add(secondUserForAdd);
        } else {
            returnUsers.addAll(availableUsers.subList(0, 2));
        }

        return returnUsers;
    }

    private List<User> getAvailableUsersForDuty(Set<User> usersNot) {
        Set<Long> excludedUserIds = usersNot.stream()
                .map(User::getId)
                .collect(Collectors.toSet());

        return userRepository.all()
                .filter("self.isActiveDuty = true AND self.alreadyInDuty = false AND self.id NOT IN :excludedUserIds")
                .bind("excludedUserIds", excludedUserIds)
                .fetch(2);
    }

    private void resetDutyFlagsIfAllInDuty(List<User> activeUsers) {
        if (allUsersAlreadyInDuty(activeUsers)) {
            resetAllDutyFlags(activeUsers);
        }
    }

    private boolean allUsersAlreadyInDuty(List<User> activeUsers) {
        return activeUsers.stream().allMatch(User::getAlreadyInDuty);
    }

    private void resetAllDutyFlags(List<User> users) {
        users.forEach(user -> {
            user.setAlreadyInDuty(false);
            userRepository.save(user);
        });
    }

    private void resetAllDutyFlags() {
        List<User> activeUsers = userRepository.all()
                .filter("self.isActiveDuty = true")
                .fetch();

        activeUsers.forEach(user -> {
            user.setAlreadyInDuty(false);
            userRepository.save(user);
        });
    }

    private void assignDutyToUsers(List<User> users) {
        users.forEach(user -> {
            user.setAlreadyInDuty(true);
            userRepository.save(user);
        });
    }

    private void createNewDuty(LocalDate startOfWeek, LocalDate endOfWeek, List<User> users) {
        Duty newDuty = new Duty();
        newDuty.setDateStart(startOfWeek);
        newDuty.setDateEnd(endOfWeek);
        newDuty.setUsers(Set.copyOf(users));

        dutyRepository.save(newDuty);
    }


}
