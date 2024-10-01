package com.axelor.apps.pbproject.db.repo;

import com.axelor.apps.businessproject.service.ProjectTaskProgressUpdateService;
import com.axelor.apps.businesssupport.db.repo.ProjectTaskBusinessSupportRepository;
import com.axelor.apps.project.db.ProjectTask;
import com.axelor.apps.pbproject.enums.StatusNames;
import com.google.inject.Inject;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class PetaProjectTaskProjectRepository extends ProjectTaskBusinessSupportRepository {

    @Inject
    public PetaProjectTaskProjectRepository(ProjectTaskProgressUpdateService projectTaskProgressUpdateService) {
        super(projectTaskProgressUpdateService);
    }

    @Override
    public ProjectTask save(ProjectTask projectTask) {
        String statusName = projectTask.getStatus().getName();

        if (StatusNames.DONE.getStatus().equals(statusName)) {
            projectTask.setEndDateTime(LocalDateTime.now());
            projectTask.setTaskEndDate(LocalDate.now());
        } else if (StatusNames.IN_PROGRESS.getStatus().equals(statusName)) {
            projectTask.setStartDateTime(LocalDateTime.now());
            projectTask.setTaskDate(LocalDate.now());
        } else if (StatusNames.NEW.getStatus().equals(statusName)) {
            projectTask.setEndDateTime(null);
            projectTask.setStartDateTime(null);
            projectTask.setTaskDate(null);
            projectTask.setTaskEndDate(null);
        }

        return super.save(projectTask);
    }
}
