package com.epam.ta.reportportal.ws.controller;

import com.epam.ta.reportportal.security.AccessEntryBuilder;
import com.epam.ta.reportportal.security.IllegalUserAccessEntry;
import com.epam.ta.reportportal.ws.BaseMvcTest;
import com.epam.ta.reportportal.ws.model.integration.IntegrationRQ;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql({"/db/integration/integration-fill.sql", "/db/security/authorization_verification.sql"})
public class IntegrationControllerAuthorizationTest extends BaseMvcTest {

    @Autowired
    private ObjectMapper objectMapper;

    private IntegrationRQ getIntegrationRQSample() {
        IntegrationRQ request = new IntegrationRQ();
        request.setName("email");
        Map<String, Object> params = new HashMap<>();
        params.put("param1", "value");
        params.put("param2", "lalala");
        request.setIntegrationParams(params);
        request.setEnabled(true);
        return request;
    }

    @Test
    void createGlobalIntegrationAuthorization() throws Exception {
        doNothing().when(emailService).testConnection();

        final String integrationUrl = "/v1/integration/email";

        for (IllegalUserAccessEntry entry : AccessEntryBuilder.createAccessEntries(oAuthHelper)) {
            mockMvc.perform(post(integrationUrl).with(token(entry.getAccessToken()))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(getIntegrationRQSample())))
                    .andExpect(status().is(entry.getAccessStatus()));
        }
    }

    @Test
    void createProjectIntegrationAuthorization() throws Exception {
        doNothing().when(emailService).testConnection();

        final String integrationUrl = "/v1/integration/default_personal/email";

        for (IllegalUserAccessEntry entry : AccessEntryBuilder.createAccessEntries(oAuthHelper)) {
            mockMvc.perform(post(integrationUrl).with(token(entry.getAccessToken()))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(getIntegrationRQSample())))
                    .andExpect(status().is(entry.getAccessStatus()));
        }
    }

    @Test
    void updateGlobalIntegrationAuthorization() throws Exception {
        doNothing().when(emailService).testConnection();

        final String integrationUrl = "/v1/integration/7";

        for (IllegalUserAccessEntry entry : AccessEntryBuilder.createAccessEntries(oAuthHelper)) {
            mockMvc.perform(put(integrationUrl).with(token(entry.getAccessToken()))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(getIntegrationRQSample())))
                    .andExpect(status().is(entry.getAccessStatus()));
        }
    }

    @Test
    void updateProjectIntegrationAuthorization() throws Exception {
        doNothing().when(emailService).testConnection();

        final String integrationUrl = "/v1/integration/default_personal/8";

        for (IllegalUserAccessEntry entry : AccessEntryBuilder.createAccessEntries(oAuthHelper)) {
            mockMvc.perform(put(integrationUrl).with(token(entry.getAccessToken()))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(getIntegrationRQSample())))
                    .andExpect(status().is(entry.getAccessStatus()));
        }
    }

    @Test
    void getAllGlobalAuthorization() throws Exception {
        for (IllegalUserAccessEntry entry : AccessEntryBuilder.createAccessEntries(oAuthHelper)) {
            mockMvc.perform(get("/v1/integration/global/all")
                    .with(token(entry.getAccessToken())))
                    .andExpect(status().is(entry.getAccessStatus()));
        }
    }

    @Test
    void getAllGlobalByTypeAuthorization() throws Exception {
        for (IllegalUserAccessEntry entry : AccessEntryBuilder.createAccessEntries(oAuthHelper)) {
            mockMvc.perform(get("/v1/integration/global/all/jira")
                    .with(token(entry.getAccessToken())))
                    .andExpect(status().is(entry.getAccessStatus()));
        }
    }

    @Test
    void getAllProjectAuthorization() throws Exception {
        for (IllegalUserAccessEntry entry : AccessEntryBuilder.createAccessEntries(oAuthHelper)) {
            mockMvc.perform(get("/v1/integration/project/superadmin_personal/all")
                    .with(token(entry.getAccessToken())))
                    .andExpect(status().is(entry.getAccessStatus()));
        }
    }

    @Test
    void getAllProjectByTypeAuthorization() throws Exception {
        for (IllegalUserAccessEntry entry : AccessEntryBuilder.createAccessEntries(oAuthHelper)) {
            mockMvc.perform(get("/v1/integration/project/superadmin_personal/all/jira")
                    .with(token(entry.getAccessToken())))
                    .andExpect(status().is(entry.getAccessStatus()));
        }
    }

    @Test
    void getGlobalIntegrationAuthorization() throws Exception {
        for (IllegalUserAccessEntry entry : AccessEntryBuilder.createAccessEntries(oAuthHelper)) {
            mockMvc.perform(get("/v1/integration/7")
                    .with(token(entry.getAccessToken())))
                    .andExpect(status().is(entry.getAccessStatus()));
        }
    }

    @Test
    void deleteGlobalIntegrationAuthorization() throws Exception {
        for (IllegalUserAccessEntry entry : AccessEntryBuilder.createAccessEntries(oAuthHelper)) {
            mockMvc.perform(delete("/v1/integration/7")
                    .with(token(entry.getAccessToken())))
                    .andExpect(status().is(entry.getAccessStatus()));
        }
    }

    @Test
    void deleteAllIntegrationsAuthorization() throws Exception {
        for (IllegalUserAccessEntry entry : AccessEntryBuilder.createAccessEntries(oAuthHelper)) {
            mockMvc.perform(delete("/v1/integration/all/email")
                    .with(token(entry.getAccessToken())))
                    .andExpect(status().is(entry.getAccessStatus()));
        }
    }

    @Test
    void getProjectIntegrationAuthorization() throws Exception {
        for (IllegalUserAccessEntry entry : AccessEntryBuilder.createAccessEntries(oAuthHelper)) {
            mockMvc.perform(get("/v1/integration/default_personal/8")
                    .with(token(entry.getAccessToken())))
                    .andExpect(status().is(entry.getAccessStatus()));
        }

    }

    @Test
    void testProjectIntegrationConnectionAuthorization() throws Exception {
        for (IllegalUserAccessEntry entry : AccessEntryBuilder.createAccessEntries(oAuthHelper)) {
            mockMvc.perform(get("/v1/integration/default_personal/8/connection/test")
                    .with(token(entry.getAccessToken())))
                    .andExpect(status().is(entry.getAccessStatus()));
        }
    }


    @Test
    void deleteProjectIntegrationAuthorization() throws Exception {
        for (IllegalUserAccessEntry entry : AccessEntryBuilder.createAccessEntries(oAuthHelper)) {
            mockMvc.perform(delete("/v1/integration/default_personal/8")
                    .with(token(entry.getAccessToken())))
                    .andExpect(status().is(entry.getAccessStatus()));
        }
    }

    @Test
    void deleteAllProjectIntegrationsAuthorization() throws Exception {
        for (IllegalUserAccessEntry entry : AccessEntryBuilder.createAccessEntries(oAuthHelper)) {
            mockMvc.perform(delete("/v1/integration/default_personal/all/email")
                    .with(token(entry.getAccessToken())))
                    .andExpect(status().is(entry.getAccessStatus()));
        }
    }

}
