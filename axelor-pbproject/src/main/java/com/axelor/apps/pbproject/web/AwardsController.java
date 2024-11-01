package com.axelor.apps.pbproject.web;

import com.axelor.apps.pbproject.service.AwardsService;
import com.axelor.auth.AuthUtils;
import com.axelor.auth.db.User;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.Inject;

import java.util.Map;

public class AwardsController {
    private final AwardsService awardsService;
    @Inject
    public AwardsController(AwardsService awardsService) {
        this.awardsService = awardsService;
    }

    public void getAwardsOfUser(ActionRequest actionRequest, ActionResponse actionResponse){
        User user = AuthUtils.getUser();
        Map<String, Object> data = awardsService.getUserAwards(user);
        actionResponse.setData(data);
    }
}
