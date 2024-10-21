package com.axelor.apps.pbproject.web;

import com.axelor.team.db.repo.TeamRepository;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.common.collect.Lists;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

public class TeamReportController {

    @Inject
    private TeamRepository teamRepository;

    public void reportTeamAnalytics(ActionRequest request, ActionResponse response) {
        long totalMembers = teamRepository.findByName("Analytics").getMembers().size();

        Map<String, Object> data = new HashMap<>();
        data.put("total", totalMembers);

        response.setData(Lists.newArrayList(data));
    }

    public void reportTeamDevOps(ActionRequest request, ActionResponse response) {
        long totalMembers = teamRepository.findByName("DevOps").getMembers().size();

        Map<String, Object> data = new HashMap<>();
        data.put("total", totalMembers);

        response.setData(Lists.newArrayList(data));
    }
    public void reportTeamBackend(ActionRequest request, ActionResponse response) {
        long totalMembers = teamRepository.findByName("Backend").getMembers().size();

        Map<String, Object> data = new HashMap<>();
        data.put("total", totalMembers);

        response.setData(Lists.newArrayList(data));
    }

    public void reportTeamFrontend(ActionRequest request, ActionResponse response) {
        long totalMembers = teamRepository.findByName("Frontend").getMembers().size();

        Map<String, Object> data = new HashMap<>();
        data.put("total", totalMembers);

        response.setData(Lists.newArrayList(data));
    }

    public void reportTeamPython(ActionRequest request, ActionResponse response) {
        long totalMembers = teamRepository.findByName("Python").getMembers().size();

        Map<String, Object> data = new HashMap<>();
        data.put("total", totalMembers);

        response.setData(Lists.newArrayList(data));
    }

    public void reportTeamCSharp(ActionRequest request, ActionResponse response) {
        long totalMembers = teamRepository.findByName("C#").getMembers().size();

        Map<String, Object> data = new HashMap<>();
        data.put("total", totalMembers);

        response.setData(Lists.newArrayList(data));
    }

    public void reportTeamLeaders(ActionRequest request, ActionResponse response) {
        long totalMembers = teamRepository.findByName("Leaders").getMembers().size();

        Map<String, Object> data = new HashMap<>();
        data.put("total", totalMembers);

        response.setData(Lists.newArrayList(data));
    }

    public void reportTeamAdministration(ActionRequest request, ActionResponse response) {
        long totalMembers = teamRepository.findByName("Administration").getMembers().size();

        Map<String, Object> data = new HashMap<>();
        data.put("total", totalMembers);

        response.setData(Lists.newArrayList(data));
    }
}
