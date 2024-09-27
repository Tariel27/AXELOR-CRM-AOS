package com.axelor.apps.pbproject.service;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface UserActivityService {
    List<Map<String, String>> getListActivityUser(Set<HttpSession> httpSessions);
}
