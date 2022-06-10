package com.softavail.recordingapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.softavail.recordingapi.entity.Webhook;
import com.softavail.recordingapi.repository.WebhookRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static com.softavail.recordingapi.util.RecordingUtil.INSERTED;
import static com.softavail.recordingapi.util.RecordingUtil.UPDATED;
import static org.hamcrest.Matchers.is;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
class RecordControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @AfterAll
    static void afterAll(@Autowired WebhookRepository webhookRepository) {
        webhookRepository.deleteAll();
    }

    @BeforeAll
    static void beforeAll(@Autowired WebhookRepository webhookRepository) {
        webhookRepository.deleteAll();
    }

    @Test
    void uploadFile_shouldReturnValidDto() throws Exception {
        Webhook localFile = getFile();
        MockMultipartFile file
                = new MockMultipartFile(
                "mediaFile",
                "test.docx",
                MediaType.TEXT_PLAIN_VALUE,
                "Hello, World!".getBytes());

        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        MockMultipartFile caseDetailsJson = new MockMultipartFile("metadata", "", "application/json", objectMapper.writeValueAsString(localFile).getBytes());

        mockMvc.perform(MockMvcRequestBuilders
                        .multipart("/record/upload")
                        .file(file)
                        .file(caseDetailsJson)).
                andExpect(status().
                is2xxSuccessful()).
                andDo(print()).
                andExpect(jsonPath("$.data.status").value(is(INSERTED))).
                andReturn();
    }


    @Test
    void updateFile_shouldReturnValidDto() throws Exception {
        uploadFile_shouldReturnValidDto();
        Webhook localFile = getFile();
        localFile.setStarted(1L);
        MockMultipartFile file
                = new MockMultipartFile(
                "mediaFile",
                "test.docx",
                MediaType.TEXT_PLAIN_VALUE,
                "Hello, World!".getBytes());

        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        MockMultipartFile caseDetailsJson = new MockMultipartFile("metadata", "", "application/json", objectMapper.writeValueAsString(localFile).getBytes());

        mockMvc.perform(MockMvcRequestBuilders
                        .multipart("/record/update")
                        .file(file)
                        .file(caseDetailsJson)).
                andExpect(status().
                is2xxSuccessful()).
                andDo(print()).
                andExpect(jsonPath("$.data.status").value(is(UPDATED))).
                andReturn();
    }

    @Test
    void getAllFile_shouldReturnValidDto() throws Exception {
        uploadFile_shouldReturnValidDto();
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/record/getAll")).
                andExpect(status().
                is2xxSuccessful()).
                andDo(print()).
                andReturn();
    }

    @Test
    void getFindByCallId_shouldReturnValidDto() throws Exception {
        uploadFile_shouldReturnValidDto();
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/record/findByCallId/osdcm49ti0d9kvd09f")).
                andExpect(status().
                is2xxSuccessful()).
                andDo(print()).
                andReturn();
    }

    @Test
    void deleteFile_shouldReturnValidDto() throws Exception {
        uploadFile_shouldReturnValidDto();
        mockMvc.perform(delete("/record/delete").
                        contentType(MediaType.APPLICATION_JSON).
                        content(objectMapper.writeValueAsString(getFile()))).
                andExpect(status().
                is2xxSuccessful()).
                andDo(print()).
                andReturn();
    }

    private Webhook getFile() {
        return Webhook.builder().
                filename("asodcjijfvodfv.wav").
                callId("osdcm49ti0d9kvd09f").
                from("11122334455").
                to("359888776655").
                started(1625906889L).
                duration(350).
                build();
    }
}