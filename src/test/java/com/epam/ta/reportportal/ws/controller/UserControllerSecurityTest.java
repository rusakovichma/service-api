package com.epam.ta.reportportal.ws.controller;

import com.epam.ta.reportportal.entity.integration.Integration;
import com.epam.ta.reportportal.security.authorization.AccessEntryBuilder;
import com.epam.ta.reportportal.security.authorization.IllegalUserProfile;
import com.epam.ta.reportportal.security.injection.InjectionPayloadsCompositeReader;
import com.epam.ta.reportportal.ws.BaseMvcTest;
import com.epam.ta.reportportal.ws.model.user.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql({"/db/user/user-fill.sql", "/db/security/authorization_verification.sql"})
public class UserControllerSecurityTest extends BaseMvcTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void restorePasswordPoisoning() throws Exception {
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
        return getCreateUserRQFullSample("testPassword");
    }

    private CreateUserRQFull getCreateUserRQFullSample(String password) {
        CreateUserRQFull rq = new CreateUserRQFull();
        rq.setLogin("testLogin");
        rq.setPassword(password);
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

    @Test
    void createUserByAdminWeakPasswordTest() throws Exception {
        final String weakPassword = "1111";
        CreateUserRQFull rq = getCreateUserRQFullSample(weakPassword);

        MvcResult mvcResult = mockMvc.perform(post("/v1/user")
                .with(token(oAuthHelper.getSuperadminToken()))
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(rq)))
                .andReturn();

        assertFalse(mvcResult.getResponse().getStatus() == HttpStatus.CREATED.value());
    }

    private CreateUserRQConfirm getCreateUserRQConfirm(String password) {
        CreateUserRQConfirm rq = new CreateUserRQConfirm();
        rq.setLogin("testLogin");
        rq.setPassword(password);
        rq.setFullName("Test User");
        rq.setEmail("test@domain.com");
        return rq;
    }

    private CreateUserRQConfirm getCreateUserRQConfirm() {
        return getCreateUserRQConfirm("testPassword");
    }

    @Test
    void createUserWeakPasswordTest() throws Exception {
        final String weakPassword = "1111";
        CreateUserRQConfirm rq = getCreateUserRQConfirm(weakPassword);

        MvcResult mvcResult = mockMvc.perform(
                post("/v1/user/registration?uuid=e5f98deb-8966-4b2d-ba2f-35bc69d30c06")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(rq)))
                .andReturn();

        assertFalse(mvcResult.getResponse().getStatus() == HttpStatus.CREATED.value());
    }

    private ChangePasswordRQ createChangePasswordRQSample(String password) {
        ChangePasswordRQ rq = new ChangePasswordRQ();
        rq.setOldPassword("1q2w3e");
        rq.setNewPassword(password);
        return rq;
    }

    @Test
    void changePasswordWeakPasswordTest() throws Exception {
        final String weakPassword = "1111";
        ChangePasswordRQ rq = createChangePasswordRQSample(weakPassword);

        MvcResult mvcResult = mockMvc.perform(post("/v1/user/password/change")
                .with(token(oAuthHelper.getDefaultToken()))
                .content(objectMapper.writeValueAsBytes(rq))
                .contentType(APPLICATION_JSON))
                .andReturn();

        assertFalse(mvcResult.getResponse().getStatus() == HttpStatus.OK.value());
    }

    @Test
    void getUserProjectsAuthorizationTest() throws Exception {
        EnumSet<IllegalUserProfile> profilesToTest = EnumSet.of(
                IllegalUserProfile.ANONYM,
                IllegalUserProfile.ANOTHER_PROJECT_CUSTOMER,
                IllegalUserProfile.ANOTHER_PROJECT_MEMBER);

        AccessEntryBuilder.createAccessEntries(oAuthHelper)
                .stream()
                .filter(entry -> profilesToTest.contains(entry.getIllegalUserType()))
                .forEach(entry -> {
                    try {
                        mockMvc.perform(get("/v1/user/default/projects")
                                .with(token(entry.getAccessToken())))
                                .andExpect(status().is(entry.getAccessStatus()));
                    } catch (Exception ex) {
                        throw new IllegalStateException(ex);
                    }
                })
        ;
    }

    @Test
    void findUsersInjectionTest() throws Exception {
        for (final String injectionPayload : new InjectionPayloadsCompositeReader()) {
            final MvcResult result =
                    mockMvc.perform(get("/v1/user/search?term=" + injectionPayload)
                            .with(token(oAuthHelper.getSuperadminToken())))
                            .andReturn();

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


}