package com.axelor.apps.pbproject.web;

import com.axelor.apps.project.db.repo.ProjectRepository;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.common.collect.Lists;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

public class TotalProjectController {

    @Inject
    private ProjectRepository projectRepository;

    public void reportTotalProjects(ActionRequest request, ActionResponse response) {
        long totalProjects = projectRepository.all().count();

        Map<String, Object> data = new HashMap<>();
        data.put("total", totalProjects);

        response.setData(Lists.newArrayList(data));
    }
}
