package com.epam.ta.reportportal.security.authorization;

import com.epam.ta.reportportal.auth.OAuthHelper;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AccessEntryBuilder {

    private AccessEntryBuilder() {
    }

    public static IllegalUserAccessEntry getAccessEntry(IllegalUserProfile illegalUserProfile,
                                                        OAuthHelper oauthHelper) {
        switch (illegalUserProfile) {
            case ANONYM:
                return new IllegalUserAccessEntry(
                        illegalUserProfile,
                        oauthHelper.getAnonymousToken(), HttpStatus.UNAUTHORIZED
                );
            case ANOTHER_PROJECT_CUSTOMER:
                return new IllegalUserAccessEntry(
                        illegalUserProfile,
                        oauthHelper.getProject2CustomerToken(), HttpStatus.FORBIDDEN
                );
            case ANOTHER_PROJECT_MEMBER:
                return new IllegalUserAccessEntry(
                        illegalUserProfile,
                        oauthHelper.getProject2MemberToken(), HttpStatus.FORBIDDEN
                );
            case ANOTHER_PROJECT_MANAGER:
                return new IllegalUserAccessEntry(
                        illegalUserProfile,
                        oauthHelper.getProject2ManagerToken(), HttpStatus.FORBIDDEN
                );
            default:
                return new IllegalUserAccessEntry(
                        illegalUserProfile,
                        oauthHelper.getDefaultToken(), HttpStatus.FORBIDDEN
                );
        }
    }

    public static Collection<IllegalUserAccessEntry> createAccessEntries(OAuthHelper oauthHelper) {
        List<IllegalUserAccessEntry> entries = new ArrayList<>(
                IllegalUserProfile.values().length);

        for (IllegalUserProfile illegalUserProfile : IllegalUserProfile.values()) {
            entries.add(getAccessEntry(illegalUserProfile, oauthHelper));
        }

        return entries;
    }
}
