package com.axelor.apps.pbproject.config;

import com.axelor.app.AppSettings;

public class FaceIdConfig {
    private static final AppSettings settings = AppSettings.get();
    public static final String FACE_ID_LOGIN = settings.get("face.id.login");
    public static final String FACE_ID_PASSWORD = settings.get("face.id.password");
    public static final String FACE_ID_URL_TOKEN = settings.get("face.id.token.api");
    public static final String FACE_ID_URL_USER_CREATE = settings.get("face.id.user.create.api");
    public static final String FACE_ID_REPORT_EXCEL = settings.get("face.id.get.excel.report");
}
