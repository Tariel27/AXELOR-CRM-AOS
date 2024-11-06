package com.axelor.apps.pbproject.service.impl;

import com.axelor.apps.pbproject.service.UserPbpProjectService;
import com.axelor.auth.AuthService;
import com.axelor.auth.db.User;
import com.axelor.auth.db.repo.UserRepository;
import com.google.inject.Inject;

import java.time.LocalDateTime;
import java.util.Objects;

public class UserPbpProjectServiceImpl implements UserPbpProjectService {

    private final AuthService authService;
    private final UserRepository userRepository;

    @Inject
    public UserPbpProjectServiceImpl(AuthService authService, UserRepository userRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
    }

    @Override
    public User find(Long userId) {
        return userRepository.find(userId);
    }

    @Override
    public void changePassword(Long userId, String password) {
        if (Objects.isNull(userId) || Objects.isNull(password)) return;
        User user = this.find(userId);
        if (Objects.isNull(user)) throw new NullPointerException("User not found!");
        user.setPassword(authService.encrypt(password));
        user.setPasswordUpdatedOn(LocalDateTime.now());
        userRepository.save(user);
    }


}
