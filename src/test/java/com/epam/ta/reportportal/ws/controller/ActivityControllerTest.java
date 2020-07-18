/*
 * Copyright 2019 EPAM Systems
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.epam.ta.reportportal.ws.controller;

import com.epam.ta.reportportal.ws.BaseMvcTest;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author <a href="mailto:ihar_kahadouski@epam.com">Ihar Kahadouski</a>
 */
@Sql("/db/activity/activity-fill.sql")
class ActivityControllerTest extends BaseMvcTest {

    @Test
    void getActivityByWrongTestItemId() throws Exception {
        mockMvc.perform(get(DEFAULT_PROJECT_BASE_URL + "/activity/1111").with(token(oAuthHelper.getDefaultToken())))
                .andExpect(status().is(404));
    }

    @Test
    void getActivityByWrongProjectName() throws Exception {
        mockMvc.perform(get("/v1/wrong_project/activity/1").with(token(oAuthHelper.getDefaultToken()))).andExpect(status().is(403));
    }

    @Test
    void getTestItemActivitiesByWrongTestItem() throws Exception {
        mockMvc.perform(get(DEFAULT_PROJECT_BASE_URL + "/activity/item/1111").with(token(oAuthHelper.getDefaultToken())))
                .andExpect(status().is(404));
    }

    @Test
    void getTestItemActivitiesPositive() throws Exception {
        mockMvc.perform(get(DEFAULT_PROJECT_BASE_URL + "/activity/item/1").with(token(oAuthHelper.getDefaultToken())))
                .andExpect(status().is(200));
    }

    @Test
    void getActivityPositive() throws Exception {
        mockMvc.perform(get(DEFAULT_PROJECT_BASE_URL + "/activity/1").with(token(oAuthHelper.getDefaultToken())))
                .andExpect(status().is(200));
    }

    @Test
    void getActivitiesForProject() throws Exception {
        mockMvc.perform(get(DEFAULT_PROJECT_BASE_URL + "/activity").with(token(oAuthHelper.getDefaultToken()))).andExpect(status().is(200));
    }

    @Test
    void getItemActivitiesAuthorizationTest() throws Exception {
        final String itemActivitiesUrl = DEFAULT_PROJECT_BASE_URL + "/activity/item/1";

        mockMvc.perform(get(itemActivitiesUrl).with(token(oAuthHelper.getAnonymousToken())))
                .andExpect(status().is(401));

        String[] anotherProjectMembers = {oAuthHelper.getProject2MemberToken(), oAuthHelper.getProject2CustomerToken()};
        for (String anotherProjectMemberToken : anotherProjectMembers) {
            mockMvc.perform(get(itemActivitiesUrl).with(token(anotherProjectMemberToken)))
                    .andExpect(status().is(403));
        }
    }

    @Test
    void getActivitiesForProjectAuthorizationTest() throws Exception {
        final String activitiesUrl = DEFAULT_PROJECT_BASE_URL + "/activity";

        mockMvc.perform(get(activitiesUrl).with(token(oAuthHelper.getAnonymousToken())))
                .andExpect(status().is(401));

        String[] anotherProjectMembers = {oAuthHelper.getProject2MemberToken(), oAuthHelper.getProject2CustomerToken()};
        for (String anotherProjectMemberToken : anotherProjectMembers) {
            mockMvc.perform(get(activitiesUrl).with(token(anotherProjectMemberToken)))
                    .andExpect(status().is(403));
        }
    }
}