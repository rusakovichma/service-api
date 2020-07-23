package com.epam.ta.reportportal.security;

import org.springframework.http.HttpStatus;

public class IllegalUserAccessEntry {

    private final IllegalUserProfile illegalUserType;
    private final String accessToken;
    private final int accessStatus;

    public IllegalUserAccessEntry(IllegalUserProfile illegalUserType, String accessToken, HttpStatus accessStatus) {
        this.illegalUserType = illegalUserType;
        this.accessToken = accessToken;
        this.accessStatus = accessStatus.value();
    }

    public String getAccessToken() {
        return accessToken;
    }

    public int getAccessStatus() {
        return accessStatus;
    }

    public IllegalUserProfile getIllegalUserType() {
        return illegalUserType;
    }
}
