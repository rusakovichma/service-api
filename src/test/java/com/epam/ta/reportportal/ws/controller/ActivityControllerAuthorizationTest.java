package com.epam.ta.reportportal.ws.controller;

import com.epam.ta.reportportal.security.AccessEntryBuilder;
import com.epam.ta.reportportal.security.IllegalUserAccessEntry;
import com.epam.ta.reportportal.ws.BaseMvcTest;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql("/db/activity/activity-fill.sql")
public class ActivityControllerAuthorizationTest extends BaseMvcTest {
    @Test
    void getItemActivitiesAuthorizationTest() throws Exception {
        final String itemActivitiesUrl = DEFAULT_PROJECT_BASE_URL + "/activity/item/1";

        for (IllegalUserAccessEntry entry : AccessEntryBuilder.createAccessEntries(oAuthHelper)) {
            mockMvc.perform(get(itemActivitiesUrl).with(token(entry.getAccessToken())))
                    .andExpect(status().is(entry.getAccessStatus()));
        }
    }

    @Test
    void getItemActivityAuthorizationTest() throws Exception {
        final String itemActivitiesUrl = DEFAULT_PROJECT_BASE_URL + "/activity/1";

        for (IllegalUserAccessEntry entry : AccessEntryBuilder.createAccessEntries(oAuthHelper)) {
            mockMvc.perform(get(itemActivitiesUrl).with(token(entry.getAccessToken())))
                    .andExpect(status().is(entry.getAccessStatus()));
        }
    }

    @Test
    void getActivitiesForProjectAuthorizationTest() throws Exception {
        final String activitiesUrl = DEFAULT_PROJECT_BASE_URL + "/activity";

        for (IllegalUserAccessEntry entry : AccessEntryBuilder.createAccessEntries(oAuthHelper)) {
            mockMvc.perform(get(activitiesUrl).with(token(entry.getAccessToken())))
                    .andExpect(status().is(entry.getAccessStatus()));
        }
    }
}
