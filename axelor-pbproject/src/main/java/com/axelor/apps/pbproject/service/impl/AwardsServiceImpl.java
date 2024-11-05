package com.axelor.apps.pbproject.service.impl;

import com.axelor.apps.pbproject.db.Award;
import com.axelor.apps.pbproject.db.repo.AwardRepository;
import com.axelor.apps.pbproject.service.AwardsService;
import com.axelor.apps.pbproject.service.UserPbpProjectService;
import com.axelor.apps.pbproject.service.WorkTimeService;
import com.axelor.apps.project.db.ProjectTask;
import com.axelor.auth.AuthUtils;
import com.axelor.auth.db.User;
import com.axelor.auth.db.repo.UserRepository;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.*;

public class AwardsServiceImpl implements AwardsService {
    private final AwardRepository awardRepository;
    private final Provider<WorkTimeService> workTimeServiceProvider;
    private final UserRepository userRepository;
    private final UserPbpProjectService userPbpProjectService;
    @Inject
    public AwardsServiceImpl(AwardRepository awardRepository, Provider<WorkTimeService> workTimeServiceProvider, UserRepository userRepository, UserPbpProjectService userPbpProjectService) {
        this.awardRepository = awardRepository;
        this.workTimeServiceProvider = workTimeServiceProvider;
        this.userRepository = userRepository;
        this.userPbpProjectService = userPbpProjectService;
    }

    @Override
    @Transactional
    public void recognizeAward(ProjectTask projectTask) {
        User user = userPbpProjectService.find(AuthUtils.getUser().getId());
        BigDecimal totalWorkHours = workTimeServiceProvider.get().getTotalSumWorkHours(user);
        int taskCount = workTimeServiceProvider.get().getAllTasksOfUser(user).size();
        Set<Award> awards = new HashSet<>();

        List<AwardCondition> awardConditions = List.of(
                new AwardCondition(AwardRepository.HOURS_100_CODE, () -> totalWorkHours.compareTo(BigDecimal.valueOf(100)) >= 0),
                new AwardCondition(AwardRepository.HOURS_500_CODE, () -> totalWorkHours.compareTo(BigDecimal.valueOf(500)) >= 0),
                new AwardCondition(AwardRepository.HOURS_1000_CODE, () -> totalWorkHours.compareTo(BigDecimal.valueOf(1000)) >= 0),
                new AwardCondition(AwardRepository.HOURS_2000_CODE, () -> totalWorkHours.compareTo(BigDecimal.valueOf(2000)) >= 0),
                new AwardCondition(AwardRepository.TASKS_30_CODE, () -> taskCount >= 30),
                new AwardCondition(AwardRepository.TASKS_50_CODE, () -> taskCount >= 50),
                new AwardCondition(AwardRepository.TASKS_100_CODE, () -> taskCount >= 100)
        );

        for (AwardCondition condition : awardConditions) {
            if (condition.isConditionMet() && !hasUserAward(user, condition.getAwardCode())) {
                awards.add(awardRepository.findByCode(condition.getAwardCode()));
            }
        }

        if (!awards.isEmpty()) {
            user.getAwards().addAll(awards);
            userRepository.save(user);
        }
    }

    private boolean hasUserAward(User user, String awardCode) {
        return user.getAwards().stream().anyMatch(award -> award.getCode().equals(awardCode));
    }

    @Override
    public List<Map<String, Object>> getUserAwards(Long userId) {
        List<Map<String,Object>> data = new ArrayList<>();

        User userFromDb = userPbpProjectService.find(userId);

        List<Award> awards = new ArrayList<>(userFromDb.getAwards());
        try {
            for (Award award : awards) {
                Map<String, Object> tempData = new HashMap<>();
                tempData.put("image", award.getImageAward() != null ? new String(award.getImageAward(), "UTF-8") : null);
                tempData.put("name", award.getName());
                tempData.put("descrip", award.getDescription());
                data.add(tempData);
            }

        } catch (UnsupportedEncodingException e){
            throw new RuntimeException();
        }


        return data;
    }


    private static class AwardCondition {
        private final String awardCode;
        private final Condition condition;

        AwardCondition(String awardCode, Condition condition) {
            this.awardCode = awardCode;
            this.condition = condition;
        }

        String getAwardCode() {
            return awardCode;
        }

        boolean isConditionMet() {
            return condition.check();
        }

        @FunctionalInterface
        interface Condition {
            boolean check();
        }
    }
}

