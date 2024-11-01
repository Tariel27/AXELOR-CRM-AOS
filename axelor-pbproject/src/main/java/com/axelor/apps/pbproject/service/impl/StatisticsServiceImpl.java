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
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

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
        List<ProjectTask> projectTasks = workTimeService.getAllTasksOfUser(user);

        Double awgTaskInDay = getAwgTaskDoneInDay(projectTasks);
        data.put("awgTaskInDay", awgTaskInDay);

        String awgHoursForOneTask = getAwgHoursForOneTask(projectTasks,user);
        data.put("awgHoursForOneTask", awgHoursForOneTask);
        return data;
    }

    private String getAwgHoursForOneTask(List<ProjectTask> projectTasks, User user) {
        long doneAll = getTasksStatusDoneAll(projectTasks);
        BigDecimal totalHours = workTimeService.getTotalSumWorkHours(user);

        if (doneAll == 0 || totalHours == null || totalHours.doubleValue() == 0.0) {
            return "00:00";
        }

        double avgHoursForOneTask = totalHours.doubleValue() / doneAll;

        int hours = (int) avgHoursForOneTask;
        int minutes = (int) ((avgHoursForOneTask - hours) * 60);

        return String.format("%02d:%02d", hours, minutes);
    }



    private Double getAwgTaskDoneInDay(List<ProjectTask> projectTasks) {
        Optional<LocalDate> minDateOpt = projectTasks.stream()
                .map(ProjectTask::getTaskDate)
                .min(Comparator.naturalOrder());

        Integer daysOfStartTasksMaking = daysBetweenOfStart(minDateOpt.get());
        long tasksEndedAll = getTasksStatusDoneAll(projectTasks);

        if (tasksEndedAll == 0 || daysOfStartTasksMaking == 0) {
            return 0.0;
        }

        double avgOneTaskInDay = (double) tasksEndedAll / daysOfStartTasksMaking;

        BigDecimal roundedValue = BigDecimal.valueOf(avgOneTaskInDay).setScale(2, RoundingMode.HALF_UP);
        return roundedValue.doubleValue();
    }
    private long getTasksStatusDoneAll(List<ProjectTask> projectTasks){
        return projectTasks.stream()
                .filter(p -> p.getStatus().getName().equalsIgnoreCase(ProjectTaskRepository.DONE))
                .count();
    }

    private Integer daysBetweenOfStart(LocalDate startTaskDate){
        LocalDate localDate = LocalDate.now();
        long days = ChronoUnit.DAYS.between(startTaskDate, localDate);
        return Math.toIntExact(days);
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

}
