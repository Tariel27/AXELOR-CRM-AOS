package com.axelor.apps.pbproject.db.repo;

import com.axelor.apps.businessproject.service.ProjectTaskProgressUpdateService;
import com.axelor.apps.businesssupport.db.repo.ProjectTaskBusinessSupportRepository;
import com.axelor.apps.project.db.ProjectTask;
import com.axelor.apps.project.db.repo.ProjectTaskRepository;
import com.google.inject.Inject;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

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
        String statusName = projectTask.getStatus().getName();

        if (ProjectTaskRepository.DONE.equals(statusName)) {
            projectTask.setEndDateTime(LocalDateTime.now());
            projectTask.setTaskEndDate(LocalDate.now());
            projectTask.setProgress(BigDecimal.valueOf(100));
        } else if (ProjectTaskRepository.IN_PROGRESS.equals(statusName)) {
            projectTask.setStartDateTime(LocalDateTime.now());
            projectTask.setTaskDate(LocalDate.now());
            projectTask.setProgress(BigDecimal.valueOf(50));
        } else if (ProjectTaskRepository.NEW.equals(statusName)) {
            projectTask.setEndDateTime(null);
            projectTask.setStartDateTime(null);
            projectTask.setTaskDate(null);
            projectTask.setTaskEndDate(null);
            projectTask.setProgress(BigDecimal.valueOf(10));
        }
    }
}
