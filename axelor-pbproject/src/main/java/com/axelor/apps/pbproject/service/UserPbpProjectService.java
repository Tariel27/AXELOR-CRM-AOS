package com.axelor.apps.pbproject.service;

import com.axelor.auth.db.User;

public interface UserPbpProjectService {
    User find(Long userId);

    void changePassword(Long userId, String password);
}
