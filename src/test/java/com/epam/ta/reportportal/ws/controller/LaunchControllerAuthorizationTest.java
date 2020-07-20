package com.epam.ta.reportportal.ws.controller;

import com.epam.ta.reportportal.dao.LaunchRepository;
import com.epam.ta.reportportal.security.AccessEntryBuilder;
import com.epam.ta.reportportal.security.IllegalUserAccessEntry;
import com.epam.ta.reportportal.ws.BaseMvcTest;
import com.epam.ta.reportportal.ws.model.attribute.ItemAttributeResource;
import com.epam.ta.reportportal.ws.model.attribute.ItemAttributesRQ;
import com.epam.ta.reportportal.ws.model.launch.StartLaunchRQ;
import com.epam.ta.reportportal.ws.model.launch.UpdateLaunchRQ;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.util.Date;

import static com.epam.ta.reportportal.ws.model.launch.Mode.DEFAULT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql("/db/launch/launch-fill.sql")
public class LaunchControllerAuthorizationTest extends BaseMvcTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LaunchRepository launchRepository;

    private StartLaunchRQ validLaunchSample() {
        String name = "some launch name";
        StartLaunchRQ startLaunchRQ = new StartLaunchRQ();
        startLaunchRQ.setDescription("some description");
        startLaunchRQ.setName(name);
        startLaunchRQ.setStartTime(new Date());
        startLaunchRQ.setMode(DEFAULT);
        startLaunchRQ.setAttributes(Sets.newHashSet(new ItemAttributesRQ("key", "value")));
        return startLaunchRQ;
    }

    private UpdateLaunchRQ validUpdateLaunchRQ() {
        UpdateLaunchRQ rq = new UpdateLaunchRQ();
        rq.setMode(DEFAULT);
        rq.setDescription("description");
        rq.setAttributes(Sets.newHashSet(new ItemAttributeResource("test", "test")));
        return rq;
    }

    @Test
    void createLaunchAuthorization() throws Exception {
        for (IllegalUserAccessEntry entry : AccessEntryBuilder.createAccessEntries(oAuthHelper)) {
            mockMvc.perform(post(DEFAULT_PROJECT_BASE_URL + "/launch/")
                    .with(token(entry.getAccessToken()))
                    .content(objectMapper.writeValueAsBytes(validLaunchSample()))
                    .contentType(APPLICATION_JSON))
                    .andExpect(status().is(entry.getAccessStatus()));
        }
    }

    @Test
    void updateLaunchPositive() throws Exception {
        for (IllegalUserAccessEntry entry : AccessEntryBuilder.createAccessEntries(oAuthHelper)) {
            mockMvc.perform(put(DEFAULT_PROJECT_BASE_URL + "/launch/3/update")
                    .with(token(entry.getAccessToken()))
                    .content(objectMapper.writeValueAsBytes(validUpdateLaunchRQ()))
                    .contentType(APPLICATION_JSON))
                    .andExpect(status().is(entry.getAccessStatus()));
        }
    }

}
