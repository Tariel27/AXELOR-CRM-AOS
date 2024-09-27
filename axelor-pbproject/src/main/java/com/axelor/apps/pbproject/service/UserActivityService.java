package com.axelor.apps.pbproject.service;

import com.axelor.auth.db.User;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Set;

public interface UserActivityService {
    List<User> getListActivityUser(Set<HttpSession> httpSessions);
}
