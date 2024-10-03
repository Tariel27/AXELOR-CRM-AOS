package com.axelor.apps.pbproject.service;

import com.axelor.auth.db.User;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;

public interface FaceIdService {
    void uploadUserToFaceId(User user);
}
