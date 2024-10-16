package com.axelor.apps.pbproject.util;

import com.axelor.i18n.I18n;
import com.axelor.meta.db.MetaFile;
import com.axelor.meta.schema.actions.ActionView;

import java.util.Map;

public class ExportUtil {
    public static Map<String, Object> createResponseView(MetaFile exportFile) {
        return ActionView.define(I18n.get("Export file"))
                .model(MetaFile.class.getName())
                .add("html", "ws/rest/com.axelor.meta.db.MetaFile/" + exportFile.getId() +
                        "/content/download?v=" + exportFile.getVersion())
                .param("download", "true")
                .map();
    }
}
