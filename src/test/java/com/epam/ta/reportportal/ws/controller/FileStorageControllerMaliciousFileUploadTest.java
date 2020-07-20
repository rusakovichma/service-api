package com.epam.ta.reportportal.ws.controller;

import com.epam.ta.reportportal.util.ClassPathUtil;
import com.epam.ta.reportportal.ws.BaseMvcTest;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class FileStorageControllerMaliciousFileUploadTest extends BaseMvcTest {

    @Test
    void uploadMaliciousSvgPhoto() throws Exception {
        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.multipart("/v1/data/photo")
                .file(new MockMultipartFile("file", new ClassPathResource("image/svg/image_with_malicious_content.svg").getInputStream()))
                .contentType(MediaType.MULTIPART_FORM_DATA);

        mockMvc.perform(requestBuilder.with(token(oAuthHelper.getDefaultToken()))).andExpect(status().isBadRequest());
    }

    @Test
    void uploadMaliciousSwfPhoto() throws Exception {
        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.multipart("/v1/data/photo")
                .file(new MockMultipartFile("file", new ClassPathResource("image/swf/xssproject.swf").getInputStream()))
                .contentType(MediaType.MULTIPART_FORM_DATA);

        mockMvc.perform(requestBuilder.with(token(oAuthHelper.getDefaultToken()))).andExpect(status().isBadRequest());
    }

    @Test
    void uploadMaliciousContentPhoto() throws Exception {
        final String activeContentPath = "image/active-content";
        List<String> activeContentFiles = ClassPathUtil.getResourceFiles(activeContentPath);
        for (String activeContentFile : activeContentFiles) {
            final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.multipart("/v1/data/photo")
                    .file(new MockMultipartFile("file", new ClassPathResource(activeContentPath + File.separator + activeContentFile).getInputStream()))
                    .contentType(MediaType.MULTIPART_FORM_DATA);

            assertTrue(mockMvc.perform(requestBuilder.with(token(oAuthHelper.getDefaultToken())))
                            .andReturn().getResponse().getStatus() != HttpStatus.OK.value(),
                    String.format("File with malicious content was successfully uploaded :%s", activeContentFile));
        }
    }

    @Test
    void uploadInfectedPhoto() throws Exception {
        final String infectedContentPath = "image/infected";
        List<String> infectedContentFiles = ClassPathUtil.getResourceFiles(infectedContentPath);
        for (String infectedFile : infectedContentFiles) {
            final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.multipart("/v1/data/photo")
                    .file(new MockMultipartFile("file", new ClassPathResource(infectedContentPath + File.separator + infectedFile).getInputStream()))
                    .contentType(MediaType.MULTIPART_FORM_DATA);

            assertTrue(mockMvc.perform(requestBuilder.with(token(oAuthHelper.getDefaultToken())))
                            .andReturn().getResponse().getStatus() != HttpStatus.OK.value(),
                    String.format("Infected file was successfully uploaded :%s", infectedFile));
        }
    }

}
