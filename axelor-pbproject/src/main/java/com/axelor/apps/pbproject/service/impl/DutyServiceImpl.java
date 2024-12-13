package com.axelor.apps.pbproject.service.impl;

import com.axelor.apps.pbproject.db.Duty;
import com.axelor.apps.pbproject.db.repo.DutyRepository;
import com.axelor.apps.pbproject.service.DutyService;
import com.axelor.apps.pbproject.service.WeeklyDataProvider;
import com.axelor.auth.db.User;
import com.axelor.auth.db.repo.UserRepository;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

public class DutyServiceImpl implements DutyService, WeeklyDataProvider {

    private final DutyRepository dutyRepository;
    private final UserRepository userRepository;

    @Inject
    public DutyServiceImpl(DutyRepository dutyRepository, UserRepository userRepository) {
        this.dutyRepository = dutyRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void openWeek() {
        List<User> activeUsersForDuty = userRepository.getUserByActiveDuty().fetch();
        resetDutyFlagsIfAllInDuty(new HashSet<>(activeUsersForDuty));
        Set<User> availableUsers = getAvailableUserForDuty();
        LocalDate currentMonday = getStartOfWeek();
        LocalDate currentSunday = getEndOfWeek(currentMonday);
        createNewDuty(currentMonday, currentSunday, availableUsers);
    }

    @Override
    @Transactional
    public void closeWeek() {
        Duty duty = dutyRepository.getRecordByCurrentWeek();
        if (Objects.isNull(duty)) return;
        duty.setCurrentWeek(false);
        dutyRepository.save(duty);
        changeDutyStatus(duty.getUsers(), true);
    }

    private Set<User> getAvailableUserForDuty() {
        Set<User> returnUsers = new HashSet<>();
        returnUsers.add(userRepository.getUserByActiveDutyAndAlreadyInDutyAsk());
        returnUsers.add(userRepository.getUserByActiveDutyAndAlreadyInDutyDesk());
        return returnUsers.contains(null) ? null : returnUsers;
    }

    private void resetDutyFlagsIfAllInDuty(Set<User> activeUsers) {
        if (allUsersAlreadyInDuty(activeUsers)) {
            changeDutyStatus(activeUsers, false);
        }
    }

    private void changeDutyStatus(Set<User> activeUsers, boolean value) {
        activeUsers.forEach(user -> {
            user.setAlreadyInDuty(value);
            userRepository.save(user);
        });
    }

    private boolean allUsersAlreadyInDuty(Set<User> activeUsers) {
        return activeUsers.stream().allMatch(User::getAlreadyInDuty);
    }

    private void createNewDuty(LocalDate startOfWeek, LocalDate endOfWeek, Set<User> users) {
        Duty newDuty = new Duty();
        newDuty.setDateStart(startOfWeek);
        newDuty.setDateEnd(endOfWeek);
        newDuty.setCurrentWeek(true);
        newDuty.setUsers(users);

        dutyRepository.save(newDuty);
    }

    public LocalDate getStartOfWeek() {
        return LocalDate.now().with(DayOfWeek.MONDAY);
    }

    public LocalDate getEndOfWeek(LocalDate startOfWeek) {
        return startOfWeek.with(DayOfWeek.SUNDAY);
    }

    @Override
    public Map<String, Object> getWeekDataProvider() {
        Map<String, Object> weeklyDataMap = new HashMap<>();
        List<User> users = new ArrayList<>(dutyRepository.getRecordByCurrentWeek().getUsers());
        weeklyDataMap.put("firstUser", users.get(0).getFullName());
        weeklyDataMap.put("secondUser", users.get(1).getFullName());
        return weeklyDataMap;
    }

}
