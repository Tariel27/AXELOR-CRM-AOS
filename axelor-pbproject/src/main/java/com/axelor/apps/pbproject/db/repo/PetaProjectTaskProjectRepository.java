package com.axelor.apps.pbproject.db.repo;

import com.axelor.apps.businessproject.service.ProjectTaskProgressUpdateService;
import com.axelor.apps.businesssupport.db.repo.ProjectTaskBusinessSupportRepository;
import com.axelor.apps.project.db.ProjectTask;
import com.axelor.apps.project.db.TaskStatus;
import com.axelor.apps.project.db.repo.ProjectTaskRepository;
import com.google.inject.Inject;

import java.math.BigDecimal;
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
}
