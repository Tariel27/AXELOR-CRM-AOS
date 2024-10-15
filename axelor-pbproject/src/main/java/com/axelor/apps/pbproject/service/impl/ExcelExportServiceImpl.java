package com.axelor.apps.pbproject.service.impl;

import com.axelor.apps.pbproject.db.Duty;
import com.axelor.apps.pbproject.db.repo.DutyRepository;
import com.axelor.apps.pbproject.service.ExportExcelService;
import com.axelor.auth.db.User;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.inject.Inject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ExcelExportServiceImpl implements ExportExcelService {

    private final DutyRepository dutyRepository;

    @Inject
    public ExcelExportServiceImpl(DutyRepository dutyRepository) {
        this.dutyRepository = dutyRepository;
    }

    @Override
    public File exportExcelDutyUsers(LocalDate startDate, LocalDate endDate) {
        try {
            List<Duty> duties = dutyRepository.all()
                    .filter("self.dateStart >= :startDate AND self.dateEnd <= :endDate")
                    .bind("startDate", startDate)
                    .bind("endDate", endDate)
                    .fetch();

            WeekFields weekFields = WeekFields.of(java.util.Locale.getDefault());
            Map<Integer, List<Duty>> dutiesByWeek = duties.stream()
                    .collect(Collectors.groupingBy(duty -> duty.getDateStart().get(weekFields.weekOfWeekBasedYear())));

            File tempFile = File.createTempFile("Дежурства", ".xlsx");

            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Дежурства");

            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBoldweight((short) 0x2bc);
            headerFont.setFontHeightInPoints((short) 12);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
            headerStyle.setAlignment(CellStyle.ALIGN_CENTER);
            headerStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
            headerStyle.setBorderBottom(CellStyle.BORDER_THIN);
            headerStyle.setBorderTop(CellStyle.BORDER_THIN);
            headerStyle.setBorderLeft(CellStyle.BORDER_THIN);
            headerStyle.setBorderRight(CellStyle.BORDER_THIN);

            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setBorderBottom(CellStyle.BORDER_THIN);
            dataStyle.setBorderTop(CellStyle.BORDER_THIN);
            dataStyle.setBorderLeft(CellStyle.BORDER_THIN);
            dataStyle.setBorderRight(CellStyle.BORDER_THIN);
            dataStyle.setAlignment(CellStyle.ALIGN_CENTER);
            dataStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);

            CellStyle alternateDataStyle = workbook.createCellStyle();
            alternateDataStyle.cloneStyleFrom(dataStyle);
            alternateDataStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
            alternateDataStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);

            sheet.setColumnWidth(0, 3000);
            sheet.setColumnWidth(1, 8000);
            sheet.setColumnWidth(2, 10000);

            Row header = sheet.createRow(0);
            String[] headers = {"Номер недели", "Промежуток недели", "Дежурные"};
            for (int i = 0; i < headers.length; i++) {
                Cell headerCell = header.createCell(i);
                headerCell.setCellValue(headers[i]);
                headerCell.setCellStyle(headerStyle);
            }

            int rowIndex = 1;
            for (Map.Entry<Integer, List<Duty>> entry : dutiesByWeek.entrySet()) {
                int weekNumber = entry.getKey();
                List<Duty> weeklyDuties = entry.getValue();

                LocalDate weekStartDate = weeklyDuties.get(0).getDateStart();
                LocalDate weekEndDate = weeklyDuties.get(0).getDateEnd();

                List<String> usersList = new ArrayList<>();
                for (Duty duty : weeklyDuties) {
                    List<User> users = new ArrayList<>(duty.getUsers());
                    for (User user : users) {
                        usersList.add(user.getFullName());
                    }
                }

                String usersNames = String.join(", ", usersList);

                Row dataRow = sheet.createRow(rowIndex++);

                CellStyle rowStyle = (rowIndex % 2 == 0) ? alternateDataStyle : dataStyle;

                Cell weekNumberCell = dataRow.createCell(0);
                weekNumberCell.setCellValue(weekNumber);
                weekNumberCell.setCellStyle(rowStyle);

                Cell weekPeriodCell = dataRow.createCell(1);
                weekPeriodCell.setCellValue(weekStartDate.toString() + " - " + weekEndDate.toString());
                weekPeriodCell.setCellStyle(rowStyle);

                Cell dutyUsersCell = dataRow.createCell(2);
                dutyUsersCell.setCellValue(usersNames);
                dutyUsersCell.setCellStyle(rowStyle);
            }

            try (FileOutputStream fileOutputStream = new FileOutputStream(tempFile)) {
                workbook.write(fileOutputStream);
            }

            return tempFile;

        } catch (IOException e) {
            throw new RuntimeException("Error while exporting Excel: " + e.getMessage(), e);
        }
    }
}
