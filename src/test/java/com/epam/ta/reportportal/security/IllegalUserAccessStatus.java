package com.epam.ta.reportportal.security;

public enum IllegalUserAccessStatus {
    ANONYM(401),
    ANOTHER_PROJECT_CUSTOMER(403),
    ANOTHER_PROJECT_MEMBER(403),
    ANOTHER_PROJECT_MANAGER(403);

    private int status;

    IllegalUserAccessStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
