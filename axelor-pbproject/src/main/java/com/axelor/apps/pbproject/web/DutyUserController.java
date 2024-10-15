package com.axelor.apps.pbproject.web;

import com.axelor.apps.base.db.AdvancedExport;
import com.axelor.apps.pbproject.service.DutyService;
import com.axelor.apps.pbproject.service.ExportExcelService;
import com.axelor.auth.db.User;
import com.axelor.i18n.I18n;
import com.axelor.inject.Beans;
import com.axelor.meta.MetaFiles;
import com.axelor.meta.db.MetaFile;
import com.axelor.meta.schema.actions.ActionView;
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

    public void updateDutyUsers(ActionRequest actionRequest, ActionResponse actionResponse){
        dutyService.updateDutyUsers();
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
            actionResponse.setView(createResponseView(exportFile));

        } catch (IOException e){
            actionResponse.setError(e.getMessage());
        }
    }

    public void updateFlags(ActionRequest actionRequest, ActionResponse actionResponse){
        List<User> usersForUpdate = (ArrayList<User>) actionRequest.getContext().get("lines");
        if (Objects.isNull(usersForUpdate)){
            actionResponse.setError(I18n.get("Please select user for update!"));
            return;
        }
        Boolean alreadyDutyFlagForUsers = (boolean) actionRequest.getContext().get("editAlreadyDuty");
        Boolean activeForDutyFlagForUsers = (boolean) actionRequest.getContext().get("editAlreadyDuty");

        dutyService.updateDutyUsersFlags(usersForUpdate, activeForDutyFlagForUsers, alreadyDutyFlagForUsers);
        actionResponse.setAlert(I18n.get("Updated successfully!"));
        actionResponse.setAttr("panel-users-grid", "refresh", true);
    }

    private Map<String, Object> createResponseView(MetaFile exportFile) {
        return ActionView.define(I18n.get("Export file"))
                .model(AdvancedExport.class.getName())
                .add("html", "ws/rest/com.axelor.meta.db.MetaFile/" + exportFile.getId() +
                        "/content/download?v=" + exportFile.getVersion())
                .param("download", "true")
                .map();
    }
}
