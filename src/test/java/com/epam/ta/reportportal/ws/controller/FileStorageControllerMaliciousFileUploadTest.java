package com.epam.ta.reportportal.ws.controller;

import com.epam.ta.reportportal.security.fileupload.MaliciousFile;
import com.epam.ta.reportportal.security.fileupload.MaliciousFileUpload;
import com.epam.ta.reportportal.util.ClassPathUtil;
import com.epam.ta.reportportal.ws.BaseMvcTest;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class FileStorageControllerMaliciousFileUploadTest extends BaseMvcTest {

    @Test
    void uploadMaliciousSvgPhoto() throws Exception {
        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.multipart("/v1/data/photo")
                .file(new MockMultipartFile("file", MaliciousFileUpload.getXssSvgFile().getContent()))
                .contentType(MediaType.MULTIPART_FORM_DATA);

        mockMvc.perform(requestBuilder.with(token(oAuthHelper.getDefaultToken())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void uploadMaliciousSwfPhoto() throws Exception {
        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.multipart("/v1/data/photo")
                .file(new MockMultipartFile("file", MaliciousFileUpload.getXssSwfFile().getContent()))
                .contentType(MediaType.MULTIPART_FORM_DATA);

        mockMvc.perform(requestBuilder.with(token(oAuthHelper.getDefaultToken())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void uploadMaliciousContentPhoto() throws Exception {
        for (MaliciousFile activeContentFile : MaliciousFileUpload.getActiveContentFiles()) {
            final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.multipart("/v1/data/photo")
                    .file(new MockMultipartFile("file", activeContentFile.getContent()))
                    .contentType(MediaType.MULTIPART_FORM_DATA);

            assertTrue(mockMvc.perform(requestBuilder.with(token(oAuthHelper.getDefaultToken())))
                            .andReturn().getResponse().getStatus() != HttpStatus.OK.value(),
                    String.format("File with malicious content was successfully uploaded :%s", activeContentFile));
        }
    }

    @Test
    void uploadInfectedPhoto() throws Exception {
        for (MaliciousFile infectedFile : MaliciousFileUpload.getInfectedFiles()) {
            final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.multipart("/v1/data/photo")
                    .file(new MockMultipartFile("file", infectedFile.getContent()))
                    .contentType(MediaType.MULTIPART_FORM_DATA);

            assertTrue(mockMvc.perform(requestBuilder.with(token(oAuthHelper.getDefaultToken())))
                            .andReturn().getResponse().getStatus() != HttpStatus.OK.value(),
                    String.format("Infected file was successfully uploaded :%s", infectedFile));
        }
    }

}
