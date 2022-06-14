package com.softavail.recordingapi.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.softavail.recordingapi.dto.ByteArrayDto;
import com.softavail.recordingapi.dto.RecordDto;
import com.softavail.recordingapi.entity.Webhook;
import com.softavail.recordingapi.mapper.RecordMapper;
import com.softavail.recordingapi.model.RecordResponse;
import com.softavail.recordingapi.repository.WebhookRepository;
import com.softavail.recordingapi.service.impl.RecordServiceImpl;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class RecordServiceTest {

    private  RecordService recordService;
    private  RecordMapper recordMapper;
    private  WebhookRepository webhookRepository;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        recordMapper = Mockito.mock(RecordMapper.class);
        webhookRepository = Mockito.mock(WebhookRepository.class);
        recordService= new RecordServiceImpl(webhookRepository,recordMapper,objectMapper);
        List<String> extension = Arrays.asList("PNG","png","jpeg","jpg","docx","pdf","xlsx","ogg");
        ReflectionTestUtils.setField(recordService, "EXTENSION_LIST", extension);
    }
    @Test
    public void whenUploadFileWithValidRequest_itShouldReturnValidDto() throws IOException {
        File file = new File("src/test/resources/test.docx");
        FileInputStream input = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile("file",
                file.getName(), "text/plain", IOUtils.toByteArray(input));
        Webhook localFile = getFile();
        RecordDto localDto = recordMapper.recordToDto(localFile);
        Mockito.when(webhookRepository.save(Mockito.any())).thenReturn(localFile);
        Mockito.when(recordMapper.recordToDto(Mockito.any())).thenReturn(localDto);
        RecordResponse response = recordService.uploadFile(multipartFile,localFile);
        assertEquals(response.getData(), localDto);
    }
    @Test
    public void whenUploadFileWrongExtension_itShouldReturnError() throws IOException {
        File file = new File("src/test/resources/test.txt");
        FileInputStream input = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile("file",
                file.getName(), "text/plain", IOUtils.toByteArray(input));
        Webhook localFile = getFile();
        RecordResponse response = recordService.uploadFile(multipartFile,localFile);
        assertEquals(response.getError().getHttpStatusCode(), HttpStatus.UNPROCESSABLE_ENTITY.value());
    }


    @Test
    public void whenUpdateFileWithValidRequest_itShouldReturnValidDto() throws IOException {
        File file = new File("src/test/resources/test.txt");
        FileInputStream input = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile("file",
                file.getName(), "text/plain", IOUtils.toByteArray(input));
        Webhook updateRequest = new Webhook();
        Webhook customFile = getFile();
        RecordDto localDto = recordMapper.recordToDto(customFile);
        List<Webhook> lf = new ArrayList<>();
        lf.add(customFile);
        Mockito.when(webhookRepository.getById(Mockito.any())).thenReturn(customFile);
        Mockito.when(webhookRepository.findByCallId(Mockito.any())).thenReturn(lf);
        Mockito.when(webhookRepository.save(updateRequest)).thenReturn(customFile);
        Mockito.when(recordMapper.recordToDto(Mockito.any())).thenReturn(localDto);
        RecordResponse response = recordService.updateFile(multipartFile,customFile);
        assertEquals(response.getData(), localDto);
        customFile.setId(null);
        response = recordService.updateFile(multipartFile,customFile);
        assertEquals(response.getData(), localDto);
    }

    @Test
    public void whenUpdateFileNull_itShouldReturnError() throws IOException {
        File file = new File("src/test/resources/test.txt");
        FileInputStream input = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile("file",
                file.getName(), "text/plain", IOUtils.toByteArray(input));
        Webhook customFile = getFile();
        Mockito.when(webhookRepository.findByCallId(Mockito.any())).thenReturn(null);
        RecordResponse response = recordService.updateFile(multipartFile, customFile);
        assertEquals(response.getError().getHttpStatusCode(), HttpStatus.UNPROCESSABLE_ENTITY.value());
    }

    @Test
    public void whenGetAllFileInfoEmptyDb_itShouldReturnError() {
        Mockito.when(webhookRepository.findAll()).thenReturn(new ArrayList<>());
        RecordResponse response = recordService.getAllFileInfo();
        assertEquals(response.getError().getHttpStatusCode(), HttpStatus.UNPROCESSABLE_ENTITY.value());
    }

    @Test
    public void whenDeleteFileWithValidRequest_itShouldReturnValidDto() {
        Webhook customFile = getFile();
        RecordDto localDto = recordMapper.recordToDto(customFile);
        Mockito.when(recordMapper.recordToDto(Mockito.any())).thenReturn(localDto);
        RecordResponse response = recordService.deleteFile(customFile);
        assertEquals(response.getData(), localDto);
        customFile.setId(null);
        response = recordService.deleteFile(customFile);
        assertEquals(response.getData(), localDto);
    }

    @Test
    public void whenDeleteFileWithInValidRequest_itShouldReturnError() {
        Webhook customFile = getFile();
        customFile.setId(null);
        customFile.setCallId(null);
        RecordResponse response = recordService.deleteFile(customFile);
        assertEquals(response.getError().getHttpStatusCode(), HttpStatus.UNPROCESSABLE_ENTITY.value());
    }

    @Test
    public void whenGetFileByIdWithValidRequest_itShouldReturnValidDto() {
        Webhook customFile = getFile();
        ByteArrayDto byteArrayDto = new ByteArrayDto();
        Optional<Webhook> optFile = Optional.of(customFile);
        Mockito.when(webhookRepository.findById(Mockito.any())).thenReturn(optFile);
        Mockito.when(recordMapper.fileToByteArrayDto(Mockito.any())).thenReturn(byteArrayDto);
        RecordResponse response = recordService.getFileById(customFile.getId());
        assertEquals(response.getData(), byteArrayDto);
    }

    @Test
    public void whenGetFileByIdWithEmptyFileValidRequest_itShouldReturnError() {
        Mockito.when(webhookRepository.findById(Mockito.any())).thenReturn(null);
        RecordResponse response = recordService.getFileById("");
        assertEquals(response.getError().getHttpStatusCode(), HttpStatus.UNPROCESSABLE_ENTITY.value());
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