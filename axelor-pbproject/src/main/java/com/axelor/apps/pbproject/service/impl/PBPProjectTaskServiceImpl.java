package com.axelor.apps.pbproject.service.impl;

import com.axelor.apps.pbproject.service.PBPProjectTaskService;
import com.axelor.apps.project.db.Project;
import com.axelor.apps.project.db.ProjectTask;
import com.axelor.apps.project.db.repo.ProjectTaskRepository;
import com.axelor.auth.AuthUtils;
import com.axelor.i18n.I18n;
import com.axelor.meta.MetaFiles;
import com.axelor.meta.db.MetaFile;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class PBPProjectTaskServiceImpl implements PBPProjectTaskService {
    private final MetaFiles metaFiles;
    private final ProjectTaskRepository projectTaskRepository;

    @Inject
    public PBPProjectTaskServiceImpl(MetaFiles metaFiles, ProjectTaskRepository projectTaskRepository) {
        this.metaFiles = metaFiles;
        this.projectTaskRepository = projectTaskRepository;
    }

    @Override
    public MetaFile generateReportByPeriod(LocalDate startDate, LocalDate endDate) throws Exception {
        List<ProjectTask> tasks = projectTaskRepository.all()
                .filter("self.assignedTo = :assignedTo AND self.typeSelect = 'task' AND self.status.isCompleted IS TRUE AND " +
                        "self.taskEndDate BETWEEN :startDate AND :endDate ORDER BY self.taskEndDate ASC")
                .bind("assignedTo", AuthUtils.getUser())
                .bind("startDate", startDate)
                .bind("endDate", endDate)
                .fetch();
        return generateExcelForTasks(tasks);
    }

    @Override
    public MetaFile generateReportForCurrentMonth() throws Exception {
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate endDate = today.with(TemporalAdjusters.lastDayOfMonth());
        return generateReportByPeriod(startDate, endDate);
    }

    @Override
    @Transactional(rollbackOn = {Exception.class})
    public MetaFile generateExcelForTasks(List<ProjectTask> tasks) throws Exception {
        XSSFWorkbook excel = new XSSFWorkbook();
        Sheet sheet = excel.createSheet(I18n.get("Report of tasks") + " - " + AuthUtils.getUser().getFullName());
        createTitleRow(sheet);

        int rowNum = 1;

        for (ProjectTask task : tasks) {
            Row row = sheet.createRow(rowNum++);
            int colNum = 0;
            row.createCell(colNum++).setCellValue(task.getName());
            row.createCell(colNum++).setCellValue(task.getDescription());
            row.createCell(colNum++).setCellValue(getComplexity(task.getComplexity()));
            row.createCell(colNum++).setCellValue(getHumanHoursFromDecimalHours(task.getPlanHours()));
            row.createCell(colNum++).setCellValue(getHumanHoursFromDecimalHours(task.getFactHours()));
            row.createCell(colNum++).setCellValue(getProjectName(task.getProject()));
        }

        for (int i = 0; i < tasks.size(); i++) {
            sheet.autoSizeColumn(i);
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        excel.write(bos);
        byte[] bytes = bos.toByteArray();
        InputStream is = new ByteArrayInputStream(bytes);

        MetaFile metaFile = metaFiles.upload(is, String.format("%s_%s_%s.xlsx", I18n.get("Report of tasks"), AuthUtils.getUser().getFullName(), getCurrentDateTime()));

        Timer timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    metaFiles.delete(metaFile);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }, 60 * 1000);

        return metaFile;
    }

    private void createTitleRow(Sheet sheet) {
        Row row = sheet.createRow(0);
        row.createCell(0).setCellValue("Задача");
        row.createCell(1).setCellValue("Описание");
        row.createCell(2).setCellValue("Сложность");
        row.createCell(3).setCellValue("ЧасыПлан");
        row.createCell(4).setCellValue("ЧасыФакт");
        row.createCell(5).setCellValue("Проект");
    }

    private String getComplexity(String complexity) {
        if (complexity == null) return "";
        switch (complexity) {
            case "easy": return "Легкий";
            case "medium": return "Средний";
            case "hard": return "Сложный";
            default: return complexity;
        }
    }

    //    private String getHumanHoursFromDecimalHours(Integer seconds) {
//        if (seconds == null) return "";
//        double v = seconds.doubleValue();
//        int minutes = (int) (v * 60.0);
//        int humanHours = minutes / 60;
//        int humanMinutes = minutes % 60;
//        return String.format("%d:%02d", humanHours, humanMinutes);
//    }
    private String getHumanHoursFromDecimalHours(Integer seconds) {
        if (seconds == null) return "";
        int humanHours = seconds / 3600;
        int humanMinutes = (seconds % 3600) / 60;
        return String.format("%d:%02d", humanHours, humanMinutes);
    }

    private String getProjectName(Project project) {
        if (project == null) return "";
        return project.getName();
    }

    private String getCurrentDateTime() {
        LocalDateTime now = LocalDateTime.now();
        return now.format(formatter);
    }
}
