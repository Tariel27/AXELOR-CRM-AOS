package com.axelor.apps.pbproject.web;

import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;

public class BaseController {

    public void refreshApp(ActionRequest request, ActionResponse response) {
        response.setSignal("refresh-app", true);
    }

    public void refreshTab(ActionRequest request, ActionResponse response) {
        response.setSignal("refresh-tab", true);
    }

}
