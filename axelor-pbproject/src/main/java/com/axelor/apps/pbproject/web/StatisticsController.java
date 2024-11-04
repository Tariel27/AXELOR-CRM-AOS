package com.axelor.apps.pbproject.web;

import com.axelor.apps.pbproject.service.StatisticsService;
import com.axelor.apps.pbproject.service.UserPbpProjectService;
import com.axelor.auth.AuthUtils;
import com.axelor.auth.db.User;
import com.axelor.i18n.I18n;
import com.axelor.meta.schema.actions.ActionView;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.Inject;
import org.stringtemplate.v4.ST;

import java.util.*;

public class StatisticsController {
    private final StatisticsService service;

    @Inject
    public StatisticsController(StatisticsService service) {
        this.service = service;

    }

    public void getStatOfTasks(ActionRequest actionRequest, ActionResponse actionResponse){
        Map<String, Object> userMap = (Map<String, Object>) actionRequest.getData().get("_user");
        Long userId = ((Number) userMap.get("id")).longValue();

        if (Objects.isNull(userId)){
            actionResponse.setError(I18n.get("User or userId is null!"));
            return;
        }

        Map<String, Object> data = service.getStatOfTasks(userId);
        actionResponse.setData(data);

    }

    public void getAwgStatistics(ActionRequest actionRequest, ActionResponse actionResponse){
        Map<String, Object> userMap = (Map<String, Object>) actionRequest.getData().get("_user");
        Long userId = ((Number) userMap.get("id")).longValue();

        if (Objects.isNull(userId)){
            actionResponse.setError(I18n.get("User or userId is null!"));
            return;
        }

        Map<String, Object> data = service.getUserAwgStat(userId);
        actionResponse.setData(data);
    }

    public void getKPDTasks(ActionRequest actionRequest, ActionResponse actionResponse){
        Map<String, Object> userMap = (Map<String, Object>) actionRequest.getData().get("_user");
        Long userId = ((Number) userMap.get("id")).longValue();

        if (Objects.isNull(userId)){
            actionResponse.setError(I18n.get("User or userId is null!"));
            return;
        }

        Map<String, Object> data = service.getKPDTasks(userId);
        actionResponse.setData(data);

    }



















    public void openStatisticsOfUser(ActionRequest actionRequest,ActionResponse actionResponse){
        Map<String, Object> userContextMap = new LinkedHashMap<>();

        Integer userId = (Integer) actionRequest.getContext().get("id");
        String userCode = (String) actionRequest.getContext().get("code");
        String userFullname = (String) actionRequest.getContext().get("name");

        userContextMap.put("id", userId);
        userContextMap.put("code", userCode);
        userContextMap.put("fullName", userFullname);


        actionResponse.setView(
                ActionView.define(I18n.get("Statistics of ") + userFullname)
                        .add("dashboard", "pbp.my-statistic-dashboard")
                        .context("_user", userContextMap)
                        .map()
        );
    }
}
