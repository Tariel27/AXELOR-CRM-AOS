package com.axelor.apps.pbproject.service;

import java.io.File;
import java.time.LocalDate;

public interface ExportExcelService {
    File exportExcelDutyUsers(LocalDate startDate, LocalDate endDate);
}
