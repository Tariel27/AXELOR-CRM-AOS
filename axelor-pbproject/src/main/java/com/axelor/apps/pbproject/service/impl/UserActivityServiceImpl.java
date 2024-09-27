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
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    @Inject
    public UserActivityServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<Map<String, String>> getListActivityUser(Set<HttpSession> httpSessions) {
        List<Map<String, String>> userList = new ArrayList<>();

        List<User> allUsers = userRepository.all().fetch();
        Set<String> activeUserCodes = new HashSet<>();

        Map<String, User> activeUsersMap = new HashMap<>();
        for (User user : allUsers) {
            activeUsersMap.put(user.getCode(), user);
        }

        for (HttpSession session : httpSessions) {
            processActiveSession(session, userList, activeUserCodes, activeUsersMap);
        }

        addInactiveUsers(userList, allUsers, activeUserCodes);

        return userList;
    }

    private void processActiveSession(HttpSession session, List<Map<String, String>> userList, Set<String> activeUserCodes, Map<String, User> userMapForGetActive) {
        String userCode = extractUserCodeFromSession(session);

        if (userCode != null) {
            User user = userMapForGetActive.get(userCode);
            if (user != null) {
                addUserToList(userList, user, formatLastAccessTime(session.getLastAccessedTime()));
                activeUserCodes.add(userCode);
            }
        }
    }

    private String extractUserCodeFromSession(HttpSession session) {
        Object principalCollectionObj = session.getAttribute("org.apache.shiro.subject.support.DefaultSubjectContext_PRINCIPALS_SESSION_KEY");
        SimplePrincipalCollection principalCollection = (SimplePrincipalCollection) principalCollectionObj;
        return (String) principalCollection.getPrimaryPrincipal();
    }

    private void addUserToList(List<Map<String, String>> userList, User user, String lastActivityTime) {
        Map<String, String> userMap = new HashMap<>();
        userMap.put("fullName", user.getFullName());
        userMap.put("lastActivityTime", lastActivityTime);
        userList.add(userMap);
    }

    private String formatLastAccessTime(long lastAccessedTime) {
        long adjustedTime = lastAccessedTime + 3600000;
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
