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
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

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
                .filter(Objects::nonNull)
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

    @Override
    public Map<String, Object> getKPDTasks(Long userId) {
        Map<String, Object> data = new HashMap<>();
        User user = userService.find(userId);

        List<ProjectTask> projectTasks = workTimeService.getAllTasksOfUser(user);

        int totalTasksWithDeadline = 0;
        int completedBeforeDeadline = 0;

        for (ProjectTask task : projectTasks) {
            if (task.getDeadlineDateTime() != null && task.getEndDateTime() != null) {
                totalTasksWithDeadline++;

                if (!task.getEndDateTime().isAfter(task.getDeadlineDateTime())) {
                    completedBeforeDeadline++;
                }
            }
        }

        double efficiency = (totalTasksWithDeadline > 0)
                ? (double) completedBeforeDeadline / totalTasksWithDeadline * 100
                : 0.0;

        data.put("totalTasksWithDeadline", totalTasksWithDeadline);
        data.put("completedBeforeDeadline", completedBeforeDeadline);
        efficiency = Math.round(efficiency * 100.0) / 100.0;
        data.put("efficiency", efficiency);
        return data;
    }

    @Override
    public Map<String, Object> getLastActivityStat(Long userId) {
         Map<String, Object> data = new HashMap<>();
        User user = userService.find(userId);

        List<ProjectTask> projectTasks = workTimeService.getAllTasksOfUser(user);

        LocalDate now = LocalDate.now();
        LocalDate weekAgo = now.minusWeeks(1);
        LocalDate monthAgo = now.minusMonths(1);

        List<ProjectTask> tasksLastWeek = projectTasks.stream()
                .filter(task -> task.getStatus().getName().equalsIgnoreCase("done") && !task.getTaskEndDate().isBefore(weekAgo))
                .collect(Collectors.toList());

        List<ProjectTask> tasksLastMonth = projectTasks.stream()
                .filter(task -> task.getStatus().getName().equalsIgnoreCase("done") && !task.getTaskEndDate().isBefore(monthAgo))
                .collect(Collectors.toList());

        int completedTasksLastWeek = tasksLastWeek.size();
        double hoursSpentLastWeek = tasksLastWeek.stream()
                .mapToDouble(task -> Duration.between(task.getStartDateTime(), task.getEndDateTime()).toHours())
                .sum();

        int completedTasksLastMonth = tasksLastMonth.size();
        double hoursSpentLastMonth = tasksLastMonth.stream()
                .mapToDouble(task -> Duration.between(task.getStartDateTime(), task.getEndDateTime()).toHours())
                .sum();

        hoursSpentLastMonth= Math.round(hoursSpentLastMonth * 100.0) / 100.0;
        hoursSpentLastWeek= Math.round(hoursSpentLastWeek * 100.0) / 100.0;


        data.put("tasksCompletedLastWeek", completedTasksLastWeek);
        data.put("hoursSpentLastWeek", hoursSpentLastWeek);
        data.put("tasksCompletedLastMonth", completedTasksLastMonth);
        data.put("hoursSpentLastMonth", hoursSpentLastMonth);

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
