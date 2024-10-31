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
    private static final int DONE_STATUS_ID = 3;
    private static final int IN_TESTING_STATUS_ID = 4;
    private static final int NEW_STATUS_ID = 1;
    private static final int IN_PROGRESS_STATUS_ID = 2;
    private static final int CANCELED_STATUS_ID = 5;

    public void reportTotalTasks(ActionRequest request, ActionResponse response) {
        long totalTasks = projectTaskRepository.all().count();
        long doneTasks = projectTaskRepository.all().filter("self.status.id = ?", DONE_STATUS_ID).count();
        long inTestingTasks = projectTaskRepository.all().filter("self.status.id = ?", IN_TESTING_STATUS_ID).count();
        long newTasks = projectTaskRepository.all().filter("self.status.id = ?", NEW_STATUS_ID).count();
        long inProgressTasks = projectTaskRepository.all().filter("self.status.id = ?", IN_PROGRESS_STATUS_ID).count();
        long canceledTasks = projectTaskRepository.all().filter("self.status.id = ?", CANCELED_STATUS_ID).count();

        Map<String, Object> data = new HashMap<>();
        data.put("total", totalTasks);
        data.put("done", doneTasks);
        data.put("inTesting", inTestingTasks);
        data.put("new", newTasks);
        data.put("inProgress", inProgressTasks);
        data.put("canceled", canceledTasks);

        response.setData(Lists.newArrayList(data));
    }
}
