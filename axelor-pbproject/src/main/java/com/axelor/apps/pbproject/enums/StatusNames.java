package com.axelor.apps.pbproject.enums;

public enum StatusNames {
    NEW("New"),
    IN_PROGRESS("In progress"),
    DONE("Done"),
    CANCELED("Canceled");

    private final String status;

    StatusNames(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
