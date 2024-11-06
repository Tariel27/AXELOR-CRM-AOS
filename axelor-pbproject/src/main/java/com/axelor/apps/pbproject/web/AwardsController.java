package com.axelor.apps.pbproject.web;

import com.axelor.apps.pbproject.service.AwardsService;
import com.axelor.auth.AuthUtils;
import com.axelor.auth.db.User;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.axelor.rpc.Context;
import com.google.inject.Inject;

import java.util.List;
import java.util.Map;

public class AwardsController {
    private final AwardsService awardsService;
    @Inject
    public AwardsController(AwardsService awardsService) {
        this.awardsService = awardsService;
    }

    public void getAwardsOfUser(ActionRequest actionRequest, ActionResponse actionResponse){
        Map<String, Object> userMap = (Map<String, Object>) actionRequest.getData().get("_user");
        Long userId = ((Number) userMap.get("id")).longValue();

        List<Map<String, Object>> data = awardsService.getUserAwards(userId);
        actionResponse.setData(data);
    }
}
