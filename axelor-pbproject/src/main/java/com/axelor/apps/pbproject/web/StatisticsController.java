package com.axelor.apps.pbproject.web;

import com.axelor.apps.pbproject.service.StatisticsService;
import com.axelor.auth.AuthUtils;
import com.axelor.auth.db.User;
import com.axelor.i18n.I18n;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.Inject;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class StatisticsController {
    private final StatisticsService service;

    @Inject
    public StatisticsController(StatisticsService service) {
        this.service = service;
    }

    public void getUserAwgStat(ActionRequest actionRequest, ActionResponse actionResponse){
        User user = AuthUtils.getUser();
        if (Objects.isNull(user) || Objects.isNull(user.getId())){
            actionResponse.setError(I18n.get("User or userId is null!"));
            return;
        }

        Map<String, Object> data = service.getUserAwgStat(user.getId());

    }

    public void getStatOfTasks(ActionRequest actionRequest, ActionResponse actionResponse){
        User user = AuthUtils.getUser();
        if (Objects.isNull(user) || Objects.isNull(user.getId())){
            actionResponse.setError(I18n.get("User or userId is null!"));
            return;
        }

        Map<String, Object> data = service.getStatOfTasks(user.getId());
        actionResponse.setData(data);

    }
}
