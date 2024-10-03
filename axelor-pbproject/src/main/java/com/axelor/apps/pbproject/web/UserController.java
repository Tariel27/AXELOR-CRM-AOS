package com.axelor.apps.pbproject.web;

import com.axelor.apps.pbproject.service.FaceIdService;
import com.axelor.apps.pbproject.service.UserPbpProjectService;
import com.axelor.auth.AuthUtils;
import com.axelor.auth.db.User;
import com.axelor.i18n.I18n;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;

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
}
