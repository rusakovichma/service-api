package com.epam.ta.reportportal.ws.controller;

import com.epam.ta.reportportal.dao.ProjectRepository;
import com.epam.ta.reportportal.security.authorization.AccessEntryBuilder;
import com.epam.ta.reportportal.security.injection.GenericInjectionPayloadsReader;
import com.epam.ta.reportportal.security.authorization.IllegalUserAccessEntry;
import com.epam.ta.reportportal.security.injection.PostgreInjectionPayloadsReader;
import com.epam.ta.reportportal.ws.BaseMvcTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.http.client.Client;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql({"/db/project/project-fill.sql", "/db/security/authorization_verification.sql"})
@ExtendWith(MockitoExtension.class)
public class ProjectControllerSecurityTest extends BaseMvcTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private Client rabbitClient;

    @Autowired
    @Qualifier("analyzerRabbitTemplate")
    private RabbitTemplate rabbitTemplate;

    @AfterEach
    void after() {
        Mockito.reset(rabbitClient, rabbitTemplate);
    }

    @Test
    void getProjectUsersCustomerAuthorizationTest() throws Exception {
        mockMvc.perform(get("/v1/project/test_project/users")
                .with(token(oAuthHelper.getProject2CustomerToken())))
                .andExpect(status().isForbidden());
    }

    @Test
    void searchForUsernameGenericInjectionTest() throws Exception {
        for (final String injectionPayload : new GenericInjectionPayloadsReader()) {
            final MvcResult result = mockMvc.perform(get(
                    "/v1/project/test_project/usernames/search?term=" + injectionPayload)
                    .with(token(oAuthHelper.getSuperadminToken()))).andReturn();

            final MockHttpServletResponse response = result.getResponse();

            String[] payloadsToExclude = {"SLEEP", "#"};
            final List<String> payloadsToExcludeList = Arrays.asList(payloadsToExclude);

            boolean exclude = false;
            for (String excluded : payloadsToExcludeList) {
                if (injectionPayload.strip().startsWith(excluded) || injectionPayload.contains(excluded)) {
                    exclude = true;
                }
            }

            if (!exclude) {
                assertTrue(response.getContentAsString().contains("\"content\":[]"),
                        String.format("Response: [%s], Payload: [%s]",
                                response.getContentAsString(),
                                injectionPayload
                        ));
            }

        }
    }

    @Test
    void searchForUsernamePostgreInjectionTest() throws Exception {
        for (final String injectionPayload : new PostgreInjectionPayloadsReader()) {
            final MvcResult result = mockMvc.perform(get(
                    "/v1/project/test_project/usernames/search?term=" + injectionPayload)
                    .with(token(oAuthHelper.getSuperadminToken()))).andReturn();

            final MockHttpServletResponse response = result.getResponse();
            assertTrue(response.getContentAsString().contains("\"content\":[]"),
                    String.format("Response: [%s], Payload: [%s]",
                            response.getContentAsString(),
                            injectionPayload
                    ));
        }
    }

    @Test
    void getProjectUsersAuthorizationTest() throws Exception {
        for (IllegalUserAccessEntry entry : AccessEntryBuilder.createAccessEntries(oAuthHelper)) {
            mockMvc.perform(get("/v1/project/test_project/users")
                    .with(token(entry.getAccessToken())))
                    .andExpect(status().is(entry.getAccessStatus()));
        }
    }

    @Test
    void searchProjectNamesGenericInjectionTest() throws Exception {
        for (final String injectionPayload : new GenericInjectionPayloadsReader()) {
            final MvcResult result = mockMvc.perform(get(
                    "/v1/project/names/search?term=" + injectionPayload)
                    .with(token(oAuthHelper.getSuperadminToken()))).andReturn();

            final MockHttpServletResponse response = result.getResponse();

            String[] payloadsToExclude = {"SLEEP", "#"};
            final List<String> payloadsToExcludeList = Arrays.asList(payloadsToExclude);

            boolean exclude = false;
            for (String excluded : payloadsToExcludeList) {
                if (injectionPayload.strip().startsWith(excluded) || injectionPayload.contains(excluded)) {
                    exclude = true;
                }
            }

            if (!exclude) {
                assertTrue(response.getContentAsString().contains("[]"),
                        String.format("Response: [%s], Payload: [%s]",
                                response.getContentAsString(),
                                injectionPayload
                        ));
            }

        }
    }


}
