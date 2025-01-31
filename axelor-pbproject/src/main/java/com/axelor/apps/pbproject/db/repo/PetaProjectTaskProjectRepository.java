package com.axelor.apps.pbproject.db.repo;

import com.axelor.apps.base.service.exception.TraceBackService;
import com.axelor.apps.businessproject.service.ProjectTaskProgressUpdateService;
import com.axelor.apps.businesssupport.db.repo.ProjectTaskBusinessSupportRepository;
import com.axelor.apps.project.db.ProjectTask;
import com.axelor.apps.project.db.TaskStatus;
import com.axelor.apps.project.db.repo.ProjectTaskRepository;
import com.google.inject.Inject;

import java.time.*;
import java.util.Objects;

public class PetaProjectTaskProjectRepository extends ProjectTaskBusinessSupportRepository {

    @Inject
    public PetaProjectTaskProjectRepository(ProjectTaskProgressUpdateService projectTaskProgressUpdateService) {
        super(projectTaskProgressUpdateService);
    }

    @Override
    public ProjectTask save(ProjectTask projectTask) {
        updateProjectTaskDates(projectTask);
        setFactHour(projectTask);
        return super.save(projectTask);
    }

    private void updateProjectTaskDates(ProjectTask projectTask) {
        TaskStatus status = projectTask.getStatus();
        if ((Objects.isNull(status))) return;
        String statusName = status.getName();
        if (Objects.isNull(statusName) || statusName.isEmpty()) return;

        if (ProjectTaskRepository.DONE.equals(statusName)) {
            if (Objects.nonNull(projectTask.getTaskEndDate())) return;
            projectTask.setEndDateTime(LocalTime.now(ZoneId.of("UTC+6")));
            projectTask.setTaskEndDate(LocalDate.now());
        }

        if (ProjectTaskRepository.IN_PROGRESS.equals(statusName)) {
            if (Objects.nonNull(projectTask.getTaskDate())) return;
            projectTask.setStartDateTime(LocalTime.now(ZoneId.of("UTC+6")));
            projectTask.setTaskDate(LocalDate.now());
        }
    }

    private void setFactHour(ProjectTask projectTask) {
        if (Objects.isNull(projectTask.getTaskEndDate())) return;
        if (Objects.isNull(projectTask.getEndDateTime())) return;
        if (Objects.isNull(projectTask.getStartDateTime())) return;
        if (Objects.isNull(projectTask.getTaskDate())) return;

        LocalDateTime startDateTimeFull = LocalDateTime.of(projectTask.getTaskDate(), projectTask.getStartDateTime());
        LocalDateTime endDateTimeFull = LocalDateTime.of(projectTask.getTaskEndDate(), projectTask.getEndDateTime());

        Duration duration = Duration.between(startDateTimeFull, endDateTimeFull);
        if (Objects.isNull(duration)) return;
        long seconds = duration.getSeconds();
        if (seconds < 0) return;

        try {
            long totalDays = seconds / (24 * 60 * 60);
            long remainingSeconds = seconds % (24 * 60 * 60);
            long totalHours = remainingSeconds / (60 * 60);
            long totalMinutes = (remainingSeconds % (60 * 60)) / 60;
            projectTask.setFactHoursString(totalDays + " дней, " + totalHours + " часов и " + totalMinutes + " минут.");
        } catch (Exception exception) {
            TraceBackService.trace(exception);
        }

        try {
            long workSeconds = 0;
            LocalDate currentDate = projectTask.getTaskDate();
            while (!currentDate.isAfter(projectTask.getTaskEndDate())) {
                LocalDateTime startOfDay = currentDate.atTime(9, 0);
                LocalDateTime endOfDay = currentDate.atTime(18, 0);
                if (currentDate.getDayOfWeek() != DayOfWeek.SATURDAY && currentDate.getDayOfWeek() != DayOfWeek.SUNDAY) {
                    LocalDateTime actualStart = startDateTimeFull.isBefore(startOfDay) ? startOfDay : startDateTimeFull;
                    LocalDateTime actualEnd = endDateTimeFull.isAfter(endOfDay) ? endOfDay : endDateTimeFull;
                    if (actualStart.isBefore(actualEnd)) {
                        Duration workDuration = Duration.between(actualStart, actualEnd);
                        workSeconds += workDuration.getSeconds();
                    }
                }
                currentDate = currentDate.plusDays(1);
            }
            projectTask.setFactHours((int) workSeconds);
        } catch (Exception exception) {
            TraceBackService.trace(exception);
        }
    }
}
