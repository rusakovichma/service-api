package com.epam.ta.reportportal.ws.controller;

import com.epam.ta.reportportal.entity.enums.ProjectAttributeEnum;
import com.epam.ta.reportportal.entity.integration.Integration;
import com.epam.ta.reportportal.entity.item.issue.IssueType;
import com.epam.ta.reportportal.entity.project.Project;
import com.epam.ta.reportportal.entity.project.ProjectIssueType;
import com.epam.ta.reportportal.ws.BaseMvcTest;
import com.epam.ta.reportportal.ws.model.user.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.epam.ta.reportportal.commons.EntityUtils.normalizeId;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql("/db/user/user-fill.sql")
public class UserControllerSecurityTest extends BaseMvcTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void restorePasswordXForwardedHostHeaderInjection() throws Exception {
        final RestorePasswordRQ restorePasswordRQ = new RestorePasswordRQ();
        restorePasswordRQ.setEmail("defaultemail@domain.com");

        when(mailServiceFactory.getDefaultEmailService(true)).thenReturn(emailService);
        doNothing().when(emailService).sendRestorePasswordEmail(any(), any(), any(), any());

        //http://www.attacker.com#login?reset=e5f98deb-8966-4b2d-ba2f-35bc69d30c06
        mockMvc.perform(post("/v1/user/password/restore")
                .with(token(oAuthHelper.getDefaultToken()))
                .header("X-Forwarded-Host", "www.attacker.com")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(restorePasswordRQ)))
                .andExpect(status().isOk());

        ArgumentCaptor<String> subjectCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object> recipientsCaptor = ArgumentCaptor.forClass(Object.class);
        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> loginCaptor = ArgumentCaptor.forClass(String.class);

        verify(emailService).sendRestorePasswordEmail(subjectCaptor.capture(),
                (String[]) recipientsCaptor.capture(), urlCaptor.capture(), loginCaptor.capture());

        assertFalse(urlCaptor.getValue().startsWith("http://www.attacker.com#login?reset="));
    }

    @Test
    void createUserBidXForwardedHostHeaderInjection() throws Exception {
        CreateUserRQ rq = new CreateUserRQ();
        rq.setDefaultProject("default_personal");
        rq.setEmail("test@domain.com");
        rq.setRole("PROJECT_MANAGER");

        when(mailServiceFactory.getEmailService(any(Integration.class), any(Boolean.class))).thenReturn(emailService);
        doNothing().when(emailService).sendCreateUserConfirmationEmail(any(), any(), any());

        MvcResult mvcResult = mockMvc.perform(post("/v1/user/bid")
                .with(token(oAuthHelper.getDefaultToken()))
                .contentType(APPLICATION_JSON)
                .header("X-Forwarded-Host", "www.attacker.com")
                .content(objectMapper.writeValueAsBytes(rq)))
                .andExpect(status().isCreated()).andReturn();

        CreateUserBidRS createUserBidRS = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(), CreateUserBidRS.class);

        assertFalse(createUserBidRS.getBackLink().startsWith("http://www.attacker.com/ui/#registration?uuid="));
    }

    private CreateUserRQFull getCreateUserRQFullSample() {
        CreateUserRQFull rq = new CreateUserRQFull();
        rq.setLogin("testLogin");
        rq.setPassword("testPassword");
        rq.setFullName("Test User");
        rq.setEmail("test@test.com");
        rq.setAccountRole("USER");
        rq.setProjectRole("MEMBER");
        rq.setDefaultProject("default_personal");
        return rq;
    }

    @Test
    void createUserByAdminXForwardedHostHeaderInjection() throws Exception {
        CreateUserRQFull createUserRQFull = getCreateUserRQFullSample();

        when(mailServiceFactory.getDefaultEmailService(any(Boolean.class))).thenReturn(emailService);
        doNothing().when(emailService).sendCreateUserConfirmationEmail((CreateUserRQFull) any(), (String) any());

        MvcResult mvcResult = mockMvc.perform(post("/v1/user")
                .with(token(oAuthHelper.getSuperadminToken()))
                .header("X-Forwarded-Host", "www.attacker.com")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(createUserRQFull)))
                .andExpect(status().isCreated()).andReturn();

        ArgumentCaptor<CreateUserRQFull> createUserRequestCaptor = ArgumentCaptor.forClass(CreateUserRQFull.class);
        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);

        verify(emailService).sendCreateUserConfirmationEmail(createUserRequestCaptor.capture(), urlCaptor.capture());

        assertFalse(urlCaptor.getValue().startsWith("http://www.attacker.com"));
    }


}