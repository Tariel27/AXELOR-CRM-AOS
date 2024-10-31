package com.axelor.apps.pbproject.service.impl;

import com.axelor.apps.pbproject.service.StatisticsService;
import com.axelor.apps.pbproject.service.UserPbpProjectService;
import com.axelor.apps.pbproject.service.WorkTimeService;
import com.axelor.apps.project.db.ProjectTask;
import com.axelor.apps.project.db.TaskStatus;
import com.axelor.apps.project.db.repo.ProjectTaskRepository;
import com.axelor.auth.db.User;
import com.google.inject.Inject;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatisticsServiceImpl implements StatisticsService {
    private final UserPbpProjectService userService;
    private final WorkTimeService workTimeService;
    @Inject
    public StatisticsServiceImpl(UserPbpProjectService userService, WorkTimeService workTimeService) {
        this.userService = userService;
        this.workTimeService = workTimeService;
    }

    @Override
    public Map<String, Object> getUserAwgStat(Long userId) {
        Map<String, Object> data = new HashMap<>();

        User user = userService.find(userId);
        BigDecimal totalWorkHours = workTimeService.getTotalSumWorkHours(user);
        BigDecimal totalEndedTasks = workTimeService.getTotalEndedTasks(user);



        return null;
    }

    @Override
    public Map<String, Object> getStatOfTasks(Long userId) {
        Map<String,Object> data = new HashMap<>();
        User user = userService.find(userId);

        List<ProjectTask> projectTasks = workTimeService.getAllTasksOfUser(user);
        BigDecimal taskCount = BigDecimal.valueOf(projectTasks.size());
        Map<String, Integer> taskStatusCounts = getTaskStatusCounts(projectTasks);
        BigDecimal totalWorkHours = workTimeService.getTotalSumWorkHours(user);

        data.put("taskCount", taskCount);
        data.putAll(taskStatusCounts);
        data.put("totalWorkHours", totalWorkHours);

        return data;
    }



    private Map<String, Integer> getTaskStatusCounts(List<ProjectTask> projectTasks) {
        Map<String, Integer> statusCounts = new HashMap<>();

        int newCount = 0;
        int inProgressCount = 0;
        int doneCount = 0;
        int canceledCount = 0;

        for (ProjectTask task : projectTasks) {
            if (task.getStatus().getName().equalsIgnoreCase(ProjectTaskRepository.NEW)) newCount++;
            if (task.getStatus().getName().equalsIgnoreCase(ProjectTaskRepository.IN_PROGRESS)) inProgressCount++;
            if (task.getStatus().getName().equalsIgnoreCase(ProjectTaskRepository.DONE)) doneCount++;
            if (task.getStatus().getName().equalsIgnoreCase(ProjectTaskRepository.CANCELED)) canceledCount++;
        }

        statusCounts.put("new", newCount);
        statusCounts.put("inProgress", inProgressCount);
        statusCounts.put("done", doneCount);
        statusCounts.put("canceled", canceledCount);

        return statusCounts;
    }
//    private Map<String, Integer> getTaskStatusCounts(List<ProjectTask> projectTasks) {
//        return projectTasks.stream()
//                .collect(Collectors.groupingBy(
//                        task -> task.getStatus().getName(),
//                        Collectors.summingInt(task -> 1)
//                ));
//    }

}
