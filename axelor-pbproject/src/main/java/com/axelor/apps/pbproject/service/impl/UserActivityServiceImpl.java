package com.axelor.apps.pbproject.service.impl;

import com.axelor.apps.pbproject.service.UserActivityService;
import com.axelor.auth.db.repo.UserRepository;
import com.axelor.auth.db.User;
import com.google.inject.Inject;
import org.apache.shiro.subject.SimplePrincipalCollection;

import javax.servlet.http.HttpSession;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class UserActivityServiceImpl implements UserActivityService {

    private final UserRepository userRepository;

    @Inject
    public UserActivityServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> getListActivityUser(Set<HttpSession> httpSessions) {
        List<User> allUsers = userRepository.all().fetch();
        Map<String, User> userMap = allUsers.stream().collect(Collectors.toMap(User::getCode, Function.identity()));

        for (HttpSession session : httpSessions) {
            session.setMaxInactiveInterval(2880);
            processActiveSession(session, userMap);
        }

        return new ArrayList<>(userMap.values());
    }

    private void processActiveSession(HttpSession session, Map<String, User> userMap) {
        String userCode = extractUserCodeFromSession(session);
        if (Objects.isNull(userCode)) return;
        User user = userMap.get(userCode);
        if (Objects.isNull(user)) return;
        user.setLastActiveDateTime(formatLastAccessTime(session.getLastAccessedTime()));
    }

    private String extractUserCodeFromSession(HttpSession session) {
        Object principalCollectionObj = session.getAttribute("org.apache.shiro.subject.support.DefaultSubjectContext_PRINCIPALS_SESSION_KEY");
        SimplePrincipalCollection principalCollection = (SimplePrincipalCollection) principalCollectionObj;
        if (Objects.isNull(principalCollection)) return null;
        return (String) principalCollection.getPrimaryPrincipal();
    }

    private LocalDateTime formatLastAccessTime(long lastAccessedTime) {
        long adjustedTime = lastAccessedTime;
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(adjustedTime), TimeZone.getDefault().toZoneId());
    }

}
