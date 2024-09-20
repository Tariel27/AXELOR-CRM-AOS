package com.axelor.apps.pbproject.service.impl;

import com.axelor.apps.pbproject.db.UserHoursManagement;
import com.axelor.apps.pbproject.db.WorkHoursDto;
import com.axelor.apps.pbproject.service.WorkTimeService;
import com.axelor.apps.project.db.Project;
import com.axelor.auth.db.User;
import com.google.inject.Inject;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class WorkTimeServiceImpl implements WorkTimeService {
    private final EntityManager entityManager;

    @Inject
    public WorkTimeServiceImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<WorkHoursDto> getWorkHours(UserHoursManagement userHoursManagement) {
        String jpqlForHours = buildQuery(userHoursManagement);

        TypedQuery<Object[]> query = entityManager.createQuery(jpqlForHours, Object[].class);

        setQueryParameters(query, userHoursManagement);

        List<Object[]> arrUserInfo = query.getResultList();

        return mapToWorkHoursDto(arrUserInfo);
    }

    private String buildQuery(UserHoursManagement userHoursManagement) {
        StringBuilder jpql = new StringBuilder("SELECT u.fullName, p.name, SUM(pt.spentTime), COUNT(pt.id) " +
                "FROM ProjectTask pt " +
                "JOIN pt.project p " +
                "JOIN pt.assignedTo u " +
                "WHERE pt.taskDate BETWEEN :fromDate AND :toDate " +
                "AND pt.taskEndDate BETWEEN :fromDate AND :toDate ");

        if (hasMembers(userHoursManagement)) {
            jpql.append("AND u.id IN :members ");
        }

        if (hasProjects(userHoursManagement)) {
            jpql.append("AND p.id IN :projects ");
        }

        jpql.append("GROUP BY u.fullName, p.name");
        return jpql.toString();
    }

    private void setQueryParameters(TypedQuery<Object[]> query, UserHoursManagement userHoursManagement) {
        query.setParameter("fromDate", userHoursManagement.getFromDate());
        query.setParameter("toDate", userHoursManagement.getToDate());

        if (hasMembers(userHoursManagement)) {
            Set<Long> memberIds = userHoursManagement.getMembersUserSet().stream()
                    .map(User::getId)
                    .collect(Collectors.toSet());
            query.setParameter("members", memberIds);
        }

        if (hasProjects(userHoursManagement)) {
            Set<Long> projectIds = userHoursManagement.getProject().stream()
                    .map(Project::getId)
                    .collect(Collectors.toSet());
            query.setParameter("projects", projectIds);
        }
    }

    private boolean hasMembers(UserHoursManagement userHoursManagement) {
        return userHoursManagement.getMembersUserSet() != null &&
                !userHoursManagement.getMembersUserSet().isEmpty();
    }

    private boolean hasProjects(UserHoursManagement userHoursManagement) {
        return userHoursManagement.getProject() != null &&
                !userHoursManagement.getProject().isEmpty();
    }

    private List<WorkHoursDto> mapToWorkHoursDto(List<Object[]> arrUserInfo) {
        return arrUserInfo.stream()
                .map(result -> {
                    WorkHoursDto dto = new WorkHoursDto();
                    dto.setUserName((String) result[0]);
                    dto.setProjectName((String) result[1]);
                    dto.setTotalWorkedHours((BigDecimal) result[2]);
                    dto.setTaskCount((Long) result[3]);
                    dto.setReadableTime(calculateReadableTime(dto.getTotalWorkedHours()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    private String calculateReadableTime(BigDecimal totalWorkedHours) {
        if (totalWorkedHours == null) {
            return "00:00";
        }

        int totalMinutes = totalWorkedHours.multiply(BigDecimal.valueOf(60)).intValue();
        int hours = totalMinutes / 60;
        int minutes = totalMinutes % 60;

        return String.format("%02d:%02d", hours, minutes);
    }

}
