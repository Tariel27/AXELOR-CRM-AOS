package com.axelor.apps.pbproject.db.repo;

import com.axelor.apps.businessproject.service.ProjectTaskProgressUpdateService;
import com.axelor.apps.businesssupport.db.repo.ProjectTaskBusinessSupportRepository;
import com.axelor.apps.project.db.ProjectTask;
import com.axelor.apps.pbproject.enums.StatusNames;
import com.axelor.apps.project.db.repo.ProjectTaskRepository;
import com.google.inject.Inject;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class PetaProjectTaskProjectRepository extends ProjectTaskBusinessSupportRepository {

    private final ProjectTaskRepository projectTaskRepository;
    @Inject
    public PetaProjectTaskProjectRepository(ProjectTaskProgressUpdateService projectTaskProgressUpdateService, ProjectTaskRepository projectTaskRepository) {
        super(projectTaskProgressUpdateService);
        this.projectTaskRepository = projectTaskRepository;
    }

    @Override
    public ProjectTask save(ProjectTask projectTask) {
        String statusName = projectTask.getStatus().getName();
        if (projectTaskRepository.DONE.equals(statusName)) {
            projectTask.setEndDateTime(LocalDateTime.now());
            projectTask.setTaskEndDate(LocalDate.now());
        } else if (projectTaskRepository.IN_PROGRESS.equals(statusName)) {
            projectTask.setStartDateTime(LocalDateTime.now());
            projectTask.setTaskDate(LocalDate.now());
        } else if (projectTaskRepository.NEW.equals(statusName)) {
            projectTask.setEndDateTime(null);
            projectTask.setStartDateTime(null);
            projectTask.setTaskDate(null);
            projectTask.setTaskEndDate(null);
        }

        return super.save(projectTask);
    }
}
