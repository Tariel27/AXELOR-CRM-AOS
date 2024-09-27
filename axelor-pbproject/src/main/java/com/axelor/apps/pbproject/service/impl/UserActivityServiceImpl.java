package com.axelor.apps.pbproject.service.impl;

import com.axelor.apps.pbproject.service.UserActivityService;
import com.axelor.auth.db.repo.UserRepository;
import com.axelor.auth.db.User;
import com.google.inject.Inject;
import org.apache.shiro.subject.SimplePrincipalCollection;

import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.*;

public class UserActivityServiceImpl implements UserActivityService {
    private final UserRepository userRepository;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    @Inject
    public UserActivityServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<Map<String, String>> getListActivityUser(Set<HttpSession> httpSessions) {
        List<Map<String, String>> userList = new ArrayList<>();

        List<User> allUsersForOfline = userRepository.all().fetch();
        Set<String> activeUserCodes = new HashSet<>();

        for (HttpSession session : httpSessions) {
            processActiveSession(session, userList, activeUserCodes);
        }

        addInactiveUsers(userList, allUsersForOfline, activeUserCodes);

        return userList;
    }

    private void processActiveSession(HttpSession session, List<Map<String, String>> userList, Set<String> activeUserCodes) {
        String userCode = extractUserCodeFromSession(session);

        if (userCode != null) {
            User user = getUserByCode(userCode);
            if (user != null) {
                addUserToList(userList, user, formatLastAccessTime(session.getLastAccessedTime()));
                activeUserCodes.add(userCode);
            }
        }
    }

    private String extractUserCodeFromSession(HttpSession session) {
        //тут как раз таки используется shiro для получения кода, а не pac4j
        Object principalCollectionObj = session.getAttribute("org.apache.shiro.subject.support.DefaultSubjectContext_PRINCIPALS_SESSION_KEY");
        SimplePrincipalCollection principalCollection = (SimplePrincipalCollection) principalCollectionObj;
        return (String) principalCollection.getPrimaryPrincipal();
    }

    private User getUserByCode(String userCode) {
        return userRepository.all()
                .filter("code = ?", userCode)
                .fetchOne();
    }

    private void addUserToList(List<Map<String, String>> userList, User user, String lastActivityTime) {
        Map<String, String> userMap = new HashMap<>();
        userMap.put("fullName", user.getFullName());
        userMap.put("lastActivityTime", lastActivityTime);
        userList.add(userMap);
    }

    private String formatLastAccessTime(long lastAccessedTime) {
        long adjustedTime = lastAccessedTime + 3600000; //в бэке время отстает на 1 час ровно
        return dateFormat.format(new Date(adjustedTime));
    }

    private void addInactiveUsers(List<Map<String, String>> userList, List<User> allUsers, Set<String> activeUserCodes) {
        for (User user : allUsers) {
            if (!activeUserCodes.contains(user.getCode())) {
                addUserToList(userList, user, "offline");
            }
        }
    }
}
