package com.axelor.apps.pbproject.web;

import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.axelor.team.db.repo.TeamRepository;
import com.google.common.collect.Lists;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

public class TotalTeamController {

    @Inject
    private TeamRepository teamRepository;

    public void reportTotalTeams(ActionRequest request, ActionResponse response) {
        long totalTeams = teamRepository.all().count();

        Map<String, Object> data = new HashMap<>();
        data.put("total", totalTeams);

        response.setData(Lists.newArrayList(data));
    }
}
