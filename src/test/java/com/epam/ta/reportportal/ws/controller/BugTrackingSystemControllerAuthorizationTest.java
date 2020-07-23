package com.epam.ta.reportportal.ws.controller;

import com.epam.reportportal.extension.bugtracking.BtsExtension;
import com.epam.ta.reportportal.entity.integration.Integration;
import com.epam.ta.reportportal.security.authorization.AccessEntryBuilder;
import com.epam.ta.reportportal.security.authorization.IllegalUserAccessEntry;
import com.epam.ta.reportportal.ws.BaseMvcTest;
import com.epam.ta.reportportal.ws.model.externalsystem.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql({"/db/bts/bts-integration-fill.sql", "/db/security/authorization_verification.sql"})
public class BugTrackingSystemControllerAuthorizationTest extends BaseMvcTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getSetOfIntegrationSystemFieldsAuthorizationTest() throws Exception {

        Map<String, List<String>> params = Maps.newHashMap();
        params.put("issueType", Lists.newArrayList("ISSUE01"));

        when(pluginBox.getInstance("jira", BtsExtension.class)).thenReturn(java.util.Optional.ofNullable(extension));
        when(extension.getTicketFields(any(String.class), any(Integration.class))).thenReturn(Lists.newArrayList(new PostFormField()));

        for (IllegalUserAccessEntry entry : AccessEntryBuilder.createAccessEntries(oAuthHelper)) {
            mockMvc.perform(get("/v1/bts/superadmin_personal/10/fields-set")
                    .params(CollectionUtils.toMultiValueMap(params))
                    .with(token(entry.getAccessToken())))
                    .andExpect(status().is(entry.getAccessStatus()));
        }
    }

    @Test
    void createIssueAuthorizationTest() throws Exception {

        PostTicketRQ request = getPostTicketRQ();

        when(pluginBox.getInstance("jira", BtsExtension.class)).thenReturn(java.util.Optional.ofNullable(extension));
        when(extension.submitTicket(any(PostTicketRQ.class), any(Integration.class))).thenReturn(new Ticket());

        for (IllegalUserAccessEntry entry : AccessEntryBuilder.createAccessEntries(oAuthHelper)) {
            mockMvc.perform(post("/v1/bts/superadmin_personal/10/ticket")
                    .with(token(entry.getAccessToken()))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(request)))
                    .andExpect(status().is(entry.getAccessStatus()));
        }

    }

    @Test
    void getTicketAuthorizationTest() throws Exception {
        final String ticketId = "/ticket_id";

        Map<String, List<String>> params = Maps.newHashMap();
        params.put("btsUrl", Lists.newArrayList("jira.com"));
        params.put("btsProject", Lists.newArrayList("project"));

        when(pluginBox.getInstance("jira", BtsExtension.class)).thenReturn(java.util.Optional.ofNullable(extension));
        when(extension.getTicket(any(String.class), any(Integration.class))).thenReturn(java.util.Optional.of(new Ticket()));

        for (IllegalUserAccessEntry entry : AccessEntryBuilder.createAccessEntries(oAuthHelper)) {
            mockMvc.perform(get("/v1/bts/superadmin_personal/ticket" + ticketId)
                    .params(CollectionUtils.toMultiValueMap(params))
                    .with(token(entry.getAccessToken())))
                    .andExpect(status().is(entry.getAccessStatus()));
        }
    }


    private PostTicketRQ getPostTicketRQ() {
        PostTicketRQ postTicketRQ = new PostTicketRQ();
        postTicketRQ.setFields(getPostFormFields());
        postTicketRQ.setNumberOfLogs(10);
        postTicketRQ.setIsIncludeScreenshots(false);
        postTicketRQ.setIsIncludeComments(false);
        postTicketRQ.setTestItemId(1L);
        return postTicketRQ;
    }

    private List<PostFormField> getPostFormFields() {
        PostFormField field = new PostFormField("id",
                "name",
                "type",
                true,
                Lists.newArrayList("value"),
                Lists.newArrayList(new AllowedValue("id", "name"))
        );
        return Lists.newArrayList(field);
    }

}
