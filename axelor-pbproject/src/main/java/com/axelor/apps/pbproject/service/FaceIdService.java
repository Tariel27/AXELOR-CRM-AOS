package com.axelor.apps.pbproject.service;

import com.axelor.auth.db.User;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;

import java.io.File;
import java.time.LocalDate;

public interface FaceIdService {
    void uploadUserToFaceId(User user);
    File exportExcelReportFaceId(LocalDate startDate, LocalDate endDate);
}
