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

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

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
        Set<Award> awards = new HashSet<>();

        if (workTimeServiceProvider.get().getTotalSumWorkHours(user).compareTo(BigDecimal.valueOf(500)) >= 0 && !hasUser500HoursAward(user)){
            awards.add(awardRepository.findByCode(AwardRepository.HOURS_500_CODE));
        }
        if (workTimeServiceProvider.get().getTotalSumWorkHours(user).compareTo(BigDecimal.valueOf(1000)) >= 0 && !hasUser1000HoursAward(user)){
            awards.add(awardRepository.findByCode(AwardRepository.HOURS_1000_CODE));
        }
        if (awards.size() == 0){
            return;
        }

        Set<Award> awardOfUser = user.getAwards();
        awardOfUser.addAll(awards);
        userRepository.save(user);
    }

    private boolean hasUser500HoursAward(User user){
        return user.getAwards().stream().anyMatch(award -> award.getCode().equals(AwardRepository.HOURS_500_CODE));
    }

    private boolean hasUser1000HoursAward(User user){
        return user.getAwards().stream().anyMatch(award -> award.getCode().equals(AwardRepository.HOURS_1000_CODE));
    }
}
