package com.epam.ta.reportportal.ws.controller;

import com.epam.ta.reportportal.security.AccessEntryBuilder;
import com.epam.ta.reportportal.security.IllegalUserAccessEntry;
import com.epam.ta.reportportal.ws.BaseMvcTest;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PluginControllerAuthorizationTest extends BaseMvcTest {

    @Test
    void getPluginsAuthorization() throws Exception {
        for (IllegalUserAccessEntry entry : AccessEntryBuilder.createAccessEntries(oAuthHelper)) {
            mockMvc.perform(get("/v1/plugin")
                    .with(token(entry.getAccessToken())))
                    .andExpect(status().is(entry.getAccessStatus()));
        }
    }

}
