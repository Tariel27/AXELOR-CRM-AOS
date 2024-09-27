package com.axelor.apps.pbproject.web;
import com.axelor.apps.pbproject.service.UserActivityService;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.axelor.web.AppSessionListener;
import com.google.inject.Inject;

import javax.servlet.http.HttpSession;
import java.util.*;

public class UserActivityController {
    private final UserActivityService userActivityService;

    @Inject
    public UserActivityController(UserActivityService userActivityService) {
        this.userActivityService = userActivityService;
    }

    public void getListActivityUser(ActionRequest actionRequest, ActionResponse actionResponse) {
        Set<HttpSession> httpSessions = AppSessionListener.getSessions();

       List<Map<String, String>> userList = userActivityService.getListActivityUser(httpSessions);

       actionResponse.setData(userList);
    }
}
