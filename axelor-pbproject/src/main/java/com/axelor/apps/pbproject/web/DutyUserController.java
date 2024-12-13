package com.axelor.apps.pbproject.web;

import com.axelor.apps.pbproject.service.DutyService;
import com.axelor.apps.pbproject.service.ExportExcelService;
import com.axelor.apps.pbproject.util.ExportUtil;
import com.axelor.auth.db.User;
import com.axelor.i18n.I18n;
import com.axelor.inject.Beans;
import com.axelor.meta.MetaFiles;
import com.axelor.meta.db.MetaFile;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.Inject;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

public class DutyUserController {
    private final DutyService dutyService;
    private final ExportExcelService exportExcelService;

    @Inject
    public DutyUserController(DutyService dutyService, ExportExcelService exportExcelService) {
        this.dutyService = dutyService;
        this.exportExcelService = exportExcelService;
    }

    public void exportExcelDutyUsers(ActionRequest actionRequest, ActionResponse actionResponse){
        LocalDate dateStart = (LocalDate) actionRequest.getContext().get("dateStart");
        LocalDate dateEnd = (LocalDate) actionRequest.getContext().get("dateEnd");
        if (Objects.isNull(dateStart) || Objects.isNull(dateEnd)){
            actionResponse.setError("Start/end dates is null!");
        }
        try {
            File exportDutyExcel = exportExcelService.exportExcelDutyUsers(dateStart,dateEnd);
            MetaFile exportFile = Beans.get(MetaFiles.class).upload(exportDutyExcel);
            actionResponse.setView(ExportUtil.createResponseView(exportFile));

        } catch (IOException e){
            actionResponse.setError(e.getMessage());
        }
    }

}
