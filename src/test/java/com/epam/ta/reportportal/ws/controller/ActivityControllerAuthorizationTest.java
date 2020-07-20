package com.epam.ta.reportportal.ws.controller;

import com.epam.ta.reportportal.ws.BaseMvcTest;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

@Sql("/db/activity/activity-fill.sql")
public class ActivityControllerAuthorizationTest extends BaseMvcTest {
    @Test
    void getItemActivitiesAuthorizationTest() throws Exception {
        final String itemActivitiesUrl = DEFAULT_PROJECT_BASE_URL + "/activity/item/1";
        authorizationTest(itemActivitiesUrl);
    }

    @Test
    void getItemActivityAuthorizationTest() throws Exception {
        final String itemActivitiesUrl = DEFAULT_PROJECT_BASE_URL + "/activity/1";
        authorizationTest(itemActivitiesUrl);
    }

    @Test
    void getActivitiesForProjectAuthorizationTest() throws Exception {
        final String activitiesUrl = DEFAULT_PROJECT_BASE_URL + "/activity";
        authorizationTest(activitiesUrl);
    }
}
