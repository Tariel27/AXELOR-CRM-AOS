package com.axelor.apps.pbproject.web;

import com.axelor.apps.project.db.repo.ProjectTaskRepository;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.common.collect.Lists;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

public class TotalTaskController {

    @Inject
    private ProjectTaskRepository projectTaskRepository;


    public void reportTotalTasks(ActionRequest request, ActionResponse response) {
        long totalTasks = projectTaskRepository.all().count();

        Map<String, Object> data = new HashMap<>();
        data.put("total", totalTasks);

        response.setData(Lists.newArrayList(data));
    }
}
