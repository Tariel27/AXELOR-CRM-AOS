package com.axelor.apps.pbproject.observer;

import com.axelor.auth.AuthUtils;
import com.axelor.event.Observes;
import com.axelor.events.PostAction;
import com.axelor.auth.db.User;
import com.axelor.auth.db.repo.UserRepository;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;

import javax.inject.Named;
import java.time.LocalDateTime;

public class ActivityObserver {

    private final UserRepository userRepository;

    @Inject
    public ActivityObserver(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public void onLoginSuccess(@Observes @Named("mail.root.messages") PostAction event) {
        User user = AuthUtils.getUser();
            user.setLastLoginDateTime(LocalDateTime.now());
            userRepository.save(user);
    }
}
