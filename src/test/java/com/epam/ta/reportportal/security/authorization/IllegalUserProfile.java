package com.epam.ta.reportportal.security.authorization;

public enum IllegalUserProfile {
    ANONYM(401),
    ANOTHER_PROJECT_CUSTOMER(403),
    ANOTHER_PROJECT_MEMBER(403),
    ANOTHER_PROJECT_MANAGER(403);

    private int status;

    IllegalUserProfile(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
