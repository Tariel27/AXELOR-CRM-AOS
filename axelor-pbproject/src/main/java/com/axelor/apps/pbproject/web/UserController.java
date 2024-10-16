package com.axelor.apps.pbproject.web;

import com.axelor.apps.base.db.AdvancedExport;
import com.axelor.apps.pbproject.service.FaceIdService;
import com.axelor.apps.pbproject.service.UserPbpProjectService;
import com.axelor.auth.AuthUtils;
import com.axelor.auth.db.User;
import com.axelor.i18n.I18n;
import com.axelor.inject.Beans;
import com.axelor.meta.MetaFiles;
import com.axelor.meta.db.MetaFile;
import com.axelor.meta.schema.actions.ActionView;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;

public class UserController {

    private final UserPbpProjectService userPbpProjectService;
    private final FaceIdService faceIdService;

    @Inject
    public UserController(UserPbpProjectService userPbpProjectService, FaceIdService faceIdService) {
        this.userPbpProjectService = userPbpProjectService;
        this.faceIdService = faceIdService;
    }

    @Transactional(rollbackOn = Exception.class)
    public void setPassword(ActionRequest actionRequest, ActionResponse actionResponse) {
        User user = actionRequest.getContext().asType(User.class);
        if (Objects.isNull(user) || Objects.isNull(user.getId()) || Objects.isNull(user.getTransientPassword())) {
            actionResponse.setError("User/UserId/Password is empty!");
            return;
        }
        String password = user.getTransientPassword();
        userPbpProjectService.changePassword(user.getId(), password);
        actionResponse.setValue("transientPassword", null);
        actionResponse.setNotify(I18n.get("Password changed!"));
        actionResponse.setCanClose(true);
    }

    public void autoSetAssigner(ActionRequest actionRequest, ActionResponse actionResponse){
        actionResponse.setValue("assignedBy",  AuthUtils.getUser());
    }

    public void uploadUserForFaceId(ActionRequest actionRequest, ActionResponse actionResponse){
        User user = actionRequest.getContext().asType(User.class);

        try {
            if (Objects.isNull(user)) {
                throw new RuntimeException("User is null!");
            }
            if (Objects.isNull(user.getImage())) {
                throw new RuntimeException("User image is empty!");
            }

            faceIdService.uploadUserToFaceId(user);
            actionResponse.setAlert("Succesful registration.");
        } catch (RuntimeException e) {
            actionResponse.setError(e.getMessage());
        }
    }

    public void commigLeavingReport(ActionRequest actionRequest, ActionResponse actionResponse){
        LocalDate startDate = LocalDate.parse((String) actionRequest.getContext().get("startDate"));
        LocalDate endDate = LocalDate.parse((String) actionRequest.getContext().get("endDate"));
        if (Objects.isNull(startDate) || Objects.isNull(endDate)){
            actionResponse.setError(I18n.get("Start/end dates is null! Please select!"));
            return;
        }
        if (startDate.isAfter(endDate)) {
            actionResponse.setError(I18n.get("Start date cannot is als end date!"));
            return;
        }

        try {
            File fileFromService = faceIdService.getExcelReportFaceId(startDate,endDate);
            MetaFile exportFile = Beans.get(MetaFiles.class).upload(fileFromService);
            actionResponse.setView(createResponseView(exportFile));
        }  catch (IOException e) {
            actionResponse.setError(e.getMessage());
        }

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
