package com.axelor.apps.pbproject.web;

import com.axelor.apps.base.db.AdvancedExport;
import com.axelor.apps.pbproject.service.PBPProjectTaskService;
import com.axelor.i18n.I18n;
import com.axelor.meta.db.MetaFile;
import com.axelor.meta.schema.actions.ActionView;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.Inject;

import java.time.LocalDate;

public class PBPProjectTaskController {
    private final PBPProjectTaskService taskService;

    @Inject
    public PBPProjectTaskController(PBPProjectTaskService taskService) {
        this.taskService = taskService;
    }

    public void generateReportByPeriod(ActionRequest request, ActionResponse response) {
        LocalDate startDate = LocalDate.parse(request.getContext().get("startDate").toString(), taskService.dateFormatter);
        LocalDate endDate = LocalDate.parse(request.getContext().get("endDate").toString(), taskService.dateFormatter);
        try {
            MetaFile metaFile = taskService.generateReportByPeriod(startDate, endDate);
            response.setView(
                    ActionView.define(I18n.get("Export file"))
                            .model(AdvancedExport.class.getName())
                            .add("html",
                                    "ws/rest/com.axelor.meta.db.MetaFile/" + metaFile.getId()
                                            + "/content/download?v=" + metaFile.getVersion())
                            .param("download", "true").map());
        } catch (Exception e) {
            response.setError(I18n.get(e.getMessage()));
        }
    }

    public void generateReportForCurrentMonth(ActionRequest request, ActionResponse response) {
        try {
            MetaFile metaFile = taskService.generateReportForCurrentMonth();
            response.setView(
                    ActionView.define(I18n.get("Export file"))
                            .model(AdvancedExport.class.getName())
                            .add("html",
                                    "ws/rest/com.axelor.meta.db.MetaFile/" + metaFile.getId()
                                            + "/content/download?v=" + metaFile.getVersion())
                            .param("download", "true").map());
        } catch (Exception e) {
            response.setError(I18n.get(e.getMessage()));
        }
    }

}
