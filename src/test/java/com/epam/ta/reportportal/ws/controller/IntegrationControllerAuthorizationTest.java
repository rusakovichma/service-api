package com.epam.ta.reportportal.ws.controller;

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

@Sql("/db/integration/integration-fill.sql")
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
        mockMvc.perform(post(integrationUrl).with(token(oAuthHelper.getAnonymousToken()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(getIntegrationRQSample())))
                .andExpect(status().is(401));

        String[] anotherProjectMembers = {oAuthHelper.getProject2MemberToken(),
                oAuthHelper.getProject2ManagerToken(),
                oAuthHelper.getProject2CustomerToken()};
        for (String anotherProjectMemberToken : anotherProjectMembers) {
            mockMvc.perform(post(integrationUrl).with(token(anotherProjectMemberToken))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(getIntegrationRQSample())))
                    .andExpect(status().is(403));
        }
    }

    @Test
    void createProjectIntegrationAuthorization() throws Exception {
        doNothing().when(emailService).testConnection();

        final String integrationUrl = "/v1/integration/default_personal/email";
        mockMvc.perform(post(integrationUrl).with(token(oAuthHelper.getDefaultToken()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(getIntegrationRQSample())))
                .andExpect(status().is(401));

        String[] anotherProjectMembers = {oAuthHelper.getProject2MemberToken(),
                oAuthHelper.getProject2ManagerToken(),
                oAuthHelper.getProject2CustomerToken()};
        for (String anotherProjectMemberToken : anotherProjectMembers) {
            mockMvc.perform(post(integrationUrl).with(token(anotherProjectMemberToken))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(getIntegrationRQSample())))
                    .andExpect(status().is(403));
        }
    }

    @Test
    void updateGlobalIntegrationAuthorization() throws Exception {
        doNothing().when(emailService).testConnection();

        final String integrationUrl = "/v1/integration/7";

        mockMvc.perform(put(integrationUrl).with(token(oAuthHelper.getAnonymousToken()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(getIntegrationRQSample())))
                .andExpect(status().is(401));

        String[] anotherProjectMembers = {oAuthHelper.getProject2MemberToken(),
                oAuthHelper.getProject2ManagerToken(),
                oAuthHelper.getProject2CustomerToken()};
        for (String anotherProjectMemberToken : anotherProjectMembers) {
            mockMvc.perform(put(integrationUrl).with(token(anotherProjectMemberToken))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(getIntegrationRQSample())))
                    .andExpect(status().is(403));
        }
    }

    @Test
    void updateProjectIntegrationAuthorization() throws Exception {
        doNothing().when(emailService).testConnection();

        final String integrationUrl = "/v1/integration/default_personal/8";

        String[] anotherProjectMembers = {oAuthHelper.getProject2MemberToken(),
                oAuthHelper.getProject2ManagerToken(),
                oAuthHelper.getProject2CustomerToken()};
        for (String anotherProjectMemberToken : anotherProjectMembers) {
            mockMvc.perform(put(integrationUrl).with(token(anotherProjectMemberToken))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(getIntegrationRQSample())))
                    .andExpect(status().is(403));
        }
    }

    @Test
    void getAllGlobalAuthorization() throws Exception {
        String[] anotherProjectMembers = {oAuthHelper.getProject2MemberToken(),
                oAuthHelper.getProject2ManagerToken(),
                oAuthHelper.getProject2CustomerToken()};
        for (String anotherProjectMemberToken : anotherProjectMembers) {
            mockMvc.perform(get("/v1/integration/global/all")
                    .with(token(anotherProjectMemberToken)))
                    .andExpect(status().is(403));
        }
    }

    @Test
    void getAllGlobalByTypeAuthorization() throws Exception {
        String[] anotherProjectMembers = {oAuthHelper.getProject2MemberToken(),
                oAuthHelper.getProject2ManagerToken(),
                oAuthHelper.getProject2CustomerToken()};
        for (String anotherProjectMemberToken : anotherProjectMembers) {
            mockMvc.perform(get("/v1/integration/global/all/jira")
                    .with(token(anotherProjectMemberToken)))
                    .andExpect(status().is(403));
        }
    }

    @Test
    void getAllProjectAuthorization() throws Exception {
        String[] anotherProjectMembers = {oAuthHelper.getProject2MemberToken(),
                oAuthHelper.getProject2ManagerToken(),
                oAuthHelper.getProject2CustomerToken()};
        for (String anotherProjectMemberToken : anotherProjectMembers) {
            mockMvc.perform(get("/v1/integration/project/superadmin_personal/all")
                    .with(token(anotherProjectMemberToken)))
                    .andExpect(status().is(403));
        }
    }

    @Test
    void getAllProjectByTypeAuthorization() throws Exception {
        String[] anotherProjectMembers = {oAuthHelper.getProject2MemberToken(),
                oAuthHelper.getProject2ManagerToken(),
                oAuthHelper.getProject2CustomerToken()};
        for (String anotherProjectMemberToken : anotherProjectMembers) {
            mockMvc.perform(get("/v1/integration/project/superadmin_personal/all/jira")
                    .with(token(anotherProjectMemberToken)))
                    .andExpect(status().is(403));
        }
    }

    @Test
    void getGlobalIntegrationAuthorization() throws Exception {
        String[] anotherProjectMembers = {oAuthHelper.getProject2MemberToken(),
                oAuthHelper.getProject2ManagerToken(),
                oAuthHelper.getProject2CustomerToken()};
        for (String anotherProjectMemberToken : anotherProjectMembers) {
            mockMvc.perform(get("/v1/integration/7")
                    .with(token(anotherProjectMemberToken)))
                    .andExpect(status().is(403));
        }
    }

    @Test
    void deleteGlobalIntegrationAuthorization() throws Exception {
        String[] anotherProjectMembers = {oAuthHelper.getProject2MemberToken(),
                oAuthHelper.getProject2ManagerToken(),
                oAuthHelper.getProject2CustomerToken()};
        for (String anotherProjectMemberToken : anotherProjectMembers) {
            mockMvc.perform(delete("/v1/integration/7")
                    .with(token(anotherProjectMemberToken)))
                    .andExpect(status().is(403));
        }
    }

    @Test
    void deleteAllIntegrationsAuthorization() throws Exception {
        String[] anotherProjectMembers = {oAuthHelper.getProject2MemberToken(),
                oAuthHelper.getProject2ManagerToken(),
                oAuthHelper.getProject2CustomerToken()};
        for (String anotherProjectMemberToken : anotherProjectMembers) {
            mockMvc.perform(delete("/v1/integration/all/email")
                    .with(token(anotherProjectMemberToken)))
                    .andExpect(status().is(403));
        }
    }

    @Test
    void getProjectIntegrationAuthorization() throws Exception {
        String[] anotherProjectMembers = {oAuthHelper.getProject2MemberToken(),
                oAuthHelper.getProject2ManagerToken(),
                oAuthHelper.getProject2CustomerToken()};
        for (String anotherProjectMemberToken : anotherProjectMembers) {
            mockMvc.perform(get("/v1/integration/default_personal/8")
                    .with(token(anotherProjectMemberToken)))
                    .andExpect(status().is(403));
        }

    }

    @Test
    void testProjectIntegrationConnectionAuthorization() throws Exception {
        String[] anotherProjectMembers = {oAuthHelper.getProject2MemberToken(),
                oAuthHelper.getProject2ManagerToken(),
                oAuthHelper.getProject2CustomerToken()};
        for (String anotherProjectMemberToken : anotherProjectMembers) {
            mockMvc.perform(get("/v1/integration/default_personal/8/connection/test")
                    .with(token(anotherProjectMemberToken)))
                    .andExpect(status().is(403));
        }
    }


    @Test
    void deleteProjectIntegrationAuthorization() throws Exception {
        String[] anotherProjectMembers = {oAuthHelper.getProject2MemberToken(),
                oAuthHelper.getProject2ManagerToken(),
                oAuthHelper.getProject2CustomerToken()};
        for (String anotherProjectMemberToken : anotherProjectMembers) {
            mockMvc.perform(delete("/v1/integration/default_personal/8")
                    .with(token(anotherProjectMemberToken)))
                    .andExpect(status().is(403));
        }
    }

    @Test
    void deleteAllProjectIntegrationsAuthorization() throws Exception {
        String[] anotherProjectMembers = {oAuthHelper.getProject2MemberToken(),
                oAuthHelper.getProject2ManagerToken(),
                oAuthHelper.getProject2CustomerToken()};
        for (String anotherProjectMemberToken : anotherProjectMembers) {
            mockMvc.perform(delete("/v1/integration/default_personal/all/email")
                    .with(token(anotherProjectMemberToken)))
                    .andExpect(status().is(403));
        }
    }

}
