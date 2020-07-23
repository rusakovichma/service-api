package com.epam.ta.reportportal.security;

import com.epam.ta.reportportal.auth.OAuthHelper;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AccessEntryBuilder {

    private AccessEntryBuilder() {
    }

    public static Collection<IllegalUserAccessEntry> createAccessEntries(OAuthHelper oauthHelper) {
        List<IllegalUserAccessEntry> entries = new ArrayList<>(
                IllegalUserProfile.values().length);

        for (IllegalUserProfile accessStatus : IllegalUserProfile.values()) {
            switch (accessStatus) {
                case ANONYM:
                    entries.add(new IllegalUserAccessEntry(
                            accessStatus,
                            oauthHelper.getAnonymousToken(), HttpStatus.UNAUTHORIZED)
                    );
                    break;
                case ANOTHER_PROJECT_CUSTOMER:
                    entries.add(new IllegalUserAccessEntry(
                            accessStatus,
                            oauthHelper.getProject2CustomerToken(), HttpStatus.FORBIDDEN)
                    );
                    break;
                case ANOTHER_PROJECT_MEMBER:
                    entries.add(new IllegalUserAccessEntry(
                            accessStatus,
                            oauthHelper.getProject2MemberToken(), HttpStatus.FORBIDDEN)
                    );
                    break;
                case ANOTHER_PROJECT_MANAGER:
                    entries.add(new IllegalUserAccessEntry(
                            accessStatus,
                            oauthHelper.getProject2ManagerToken(), HttpStatus.FORBIDDEN)
                    );
                    break;
            }
        }

        return entries;
    }
}
