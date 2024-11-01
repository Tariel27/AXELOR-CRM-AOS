package com.axelor.apps.pbproject.db.repo;

import com.axelor.apps.businessproject.service.ProjectTaskProgressUpdateService;
import com.axelor.apps.businesssupport.db.repo.ProjectTaskBusinessSupportRepository;
import com.axelor.apps.project.db.ProjectTask;
import com.axelor.apps.project.db.repo.ProjectTaskRepository;
import com.google.inject.Inject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;

public class PetaProjectTaskProjectRepository extends ProjectTaskBusinessSupportRepository {


    @Inject
    public PetaProjectTaskProjectRepository(ProjectTaskProgressUpdateService projectTaskProgressUpdateService) {
        super(projectTaskProgressUpdateService);
    }

    @Override
    public ProjectTask save(ProjectTask projectTask) {
        updateProjectTaskDates(projectTask);
        calculateSpentTime(projectTask);
        return super.save(projectTask);
    }

    private void calculateSpentTime(ProjectTask projectTask) {
        LocalDateTime startDateTime = projectTask.getStartDateTime();
        LocalDateTime endDateTime = projectTask.getEndDateTime();

        if (startDateTime == null || endDateTime == null) {
            return;
        }
        if (projectTask.getSpentTime().compareTo(BigDecimal.ZERO) != 0){
            return;
        }

        BigDecimal spentTime = BigDecimal.ZERO;
        LocalDate currentDate = startDateTime.toLocalDate();

        if (isSameDay(startDateTime, endDateTime)) {
            spentTime = spentTime.add(calculateSameDayDuration(startDateTime, endDateTime));
        } else {
            spentTime = calculateMultiDayDuration(startDateTime, endDateTime, currentDate);
        }

        projectTask.setSpentTime(spentTime);
    }

    private boolean isSameDay(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return startDateTime.toLocalDate().equals(endDateTime.toLocalDate());
    }

    private BigDecimal calculateSameDayDuration(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        Duration duration = Duration.between(startDateTime, endDateTime);
        return convertDurationToHours(duration);
    }

    private BigDecimal calculateMultiDayDuration(LocalDateTime startDateTime, LocalDateTime endDateTime, LocalDate currentDate) {
        BigDecimal totalSpentTime = BigDecimal.ZERO;
        LocalTime workStart = LocalTime.of(9, 0);

        while (!currentDate.isAfter(endDateTime.toLocalDate())) {
            if (isWorkingDay(currentDate)) {
                if (currentDate.equals(endDateTime.toLocalDate())) {
                    totalSpentTime = totalSpentTime.add(calculateEndDayDuration(endDateTime, currentDate, workStart));
                } else {
                    totalSpentTime = totalSpentTime.add(BigDecimal.valueOf(8));
                }
            }
            currentDate = currentDate.plusDays(1);
        }

        return totalSpentTime;
    }

    private BigDecimal calculateEndDayDuration(LocalDateTime endDateTime, LocalDate currentDate, LocalTime workStart) {
        LocalDateTime startOfDayTime = LocalDateTime.of(currentDate, workStart);
        Duration duration = Duration.between(startOfDayTime, endDateTime);
        return convertDurationToHours(duration);
    }

    private BigDecimal convertDurationToHours(Duration duration) {
        long durationSeconds = duration.getSeconds();
        return BigDecimal.valueOf(durationSeconds).divide(BigDecimal.valueOf(3600), 2, RoundingMode.HALF_UP);
    }

    private boolean isWorkingDay(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek != DayOfWeek.SUNDAY;
    }


    private void updateProjectTaskDates(ProjectTask projectTask) {
        String statusName = projectTask.getStatus().getName();

        if (ProjectTaskRepository.DONE.equals(statusName)) {
            if (projectTask.getEndDateTime() == null) {
                projectTask.setEndDateTime(LocalDateTime.now());
                projectTask.setTaskEndDate(LocalDate.now());
            }
            projectTask.setProgress(BigDecimal.valueOf(100));
        } else if (ProjectTaskRepository.IN_PROGRESS.equals(statusName)) {
            if (projectTask.getStartDateTime() == null) {
                projectTask.setStartDateTime(LocalDateTime.now());
                projectTask.setTaskDate(LocalDate.now());
            }
            projectTask.setProgress(BigDecimal.valueOf(50));
        } else if (ProjectTaskRepository.NEW.equals(statusName)) {
            projectTask.setEndDateTime(null);
            projectTask.setTaskEndDate(null);
            projectTask.setProgress(BigDecimal.valueOf(10));
        }
    }
}
