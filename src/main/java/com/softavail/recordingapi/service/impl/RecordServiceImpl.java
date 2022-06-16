package com.softavail.recordingapi.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.softavail.recordingapi.entity.Webhook;
import com.softavail.recordingapi.mapper.RecordMapper;
import com.softavail.recordingapi.model.Error;
import com.softavail.recordingapi.model.RecordResponse;
import com.softavail.recordingapi.model.WebhookRequest;
import com.softavail.recordingapi.repository.WebhookRepository;
import com.softavail.recordingapi.service.RecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.*;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

import static com.softavail.recordingapi.util.RecordingUtil.*;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class RecordServiceImpl implements RecordService {

    private final WebhookRepository webhookRepository;

    private final RecordMapper recordMapper;

    private final  ObjectMapper objectMapper;

    @Value("#{'${extension.list}'.split(',')}")
    private List<String> EXTENSION_LIST;

    @Value("${file.system.directory.path}")
    private  String fileDirectory;

    @Value("${endpoint.processor}")
    private String processorEndpoint;

    @Override
    public RecordResponse uploadFile(MultipartFile mf, Webhook request) {
        try {
          if (!EXTENSION_LIST.contains(FilenameUtils.getExtension(mf.getOriginalFilename()))) {
                log.error("wrong extension for " + FilenameUtils.getExtension(mf.getOriginalFilename()));
                return new RecordResponse(FAILED, "", new Error(HttpStatus.UNPROCESSABLE_ENTITY, ERROR_UNPROCESSABLE_EXTENSION));
            }
            Webhook webhook = Webhook.builder().
                                        filename(request.getFilename()).
                                        callId(request.getCallId()).
                                        from(request.getFrom()).
                                        to(request.getTo()).
                                        started(request.getStarted()).
                                        duration(request.getDuration()).
                                        status(INSERTED).
                                        content(mf.getBytes()).
                                        fileExtension(FilenameUtils.getExtension(mf.getOriginalFilename())).
                                        build();
            Webhook webhookSaved = webhookRepository.save(webhook);
            return new RecordResponse(SUCCEED, recordMapper.recordToDto(webhookSaved), null);
        } catch (Exception ex) {
            log.error("Exception on ", ex);
            return new RecordResponse(FAILED, "", new Error(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), ex));
        }
    }

    @Override
    public RecordResponse deleteFile(Webhook deleteRequest) {
        try {
            if (deleteRequest != null && deleteRequest.getId() != null) {
                webhookRepository.deleteAllById(Collections.singleton(deleteRequest.getId()));
                return new RecordResponse(SUCCEED, recordMapper.deleteRequestToDto(deleteRequest.getId() , DELETED), null);
            }  else if (deleteRequest != null && deleteRequest.getCallId() != null) {
                webhookRepository.deleteAllByCallId(deleteRequest.getCallId() );
                return new RecordResponse(SUCCEED, recordMapper.deleteRequestToDto(deleteRequest.getCallId() , DELETED), null);
            }else {
                log.error("Please add a file name or id in request file");
                return new RecordResponse(FAILED, "", new Error(HttpStatus.UNPROCESSABLE_ENTITY, ERROR_UNPROCESSABLE_FILE_BRACE));
            }
        }
        catch (Exception ex) {
            log.error("Exception on ", ex);
            return new RecordResponse(FAILED, "", new Error(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), ex));
        }
    }

    @Override
    public RecordResponse updateFile(MultipartFile multipartFile, Webhook request) {
        try {
            if (request != null && (request.getId() != null || request.getCallId() != null)) {
                Webhook webhook;
                if(request.getId() != null){
                    webhook = webhookRepository.getById(request.getId());
                }else {
                    List<Webhook> tmpF = webhookRepository.findByCallId(request.getCallId());
                    webhook = tmpF ==null ? null : tmpF.get(0);
                }
                if (webhook == null) {
                    return new RecordResponse(FAILED, "", new Error(HttpStatus.UNPROCESSABLE_ENTITY, ERROR_FILE_NOT_IN_DB));
                }
                request.setId(webhook.getId());
                request.setStatus(UPDATED);
                request.setContent(multipartFile.getBytes());
                request.setFileExtension(FilenameUtils.getExtension(multipartFile.getOriginalFilename()));
                webhookRepository.save(request);
                return new RecordResponse(SUCCEED, recordMapper.recordToDto(request), null);
            } else {
                log.error("Please add  not empty file for update");
                return new RecordResponse(FAILED, "", new Error(HttpStatus.UNPROCESSABLE_ENTITY, ERROR_UNPROCESSABLE_FILE_BRACE));
            }
        }catch (Exception ex){
            log.error("Exception on ", ex);
            return new RecordResponse(FAILED, "", new Error(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), ex));
        }
    }

    @Override
    public RecordResponse getAllFileInfo() {
        List<Webhook> webhookList = webhookRepository.findAll();
        if (webhookList.isEmpty()) {
            log.debug("File table is empty");
            return new RecordResponse(FAILED, "", new Error(HttpStatus.UNPROCESSABLE_ENTITY, ERROR_NO_CONTENT_TABLE));
        }
        return new RecordResponse(SUCCEED, recordMapper.recordListToDtoList(webhookList), null);
    }

    @Override
    public RecordResponse getFileByCallId(String callId) {
        List<Webhook> webhook = webhookRepository.findByCallId(callId);
        if (webhook.isEmpty()) {
            log.debug("File table is empty");
            return new RecordResponse(FAILED, "", new Error(HttpStatus.UNPROCESSABLE_ENTITY, ERROR_NO_CONTENT_TABLE));
        }
        return new RecordResponse(SUCCEED, recordMapper.fileToByteArrayDto(webhook.get(0)), null);
    }

    @Override
    public RecordResponse getFileById(String id) {
        Optional<Webhook> webhook = webhookRepository.findById(id);
        if (webhook ==null || webhook.isEmpty()) {
            log.debug("File table is empty");
            return new RecordResponse(FAILED, "", new Error(HttpStatus.UNPROCESSABLE_ENTITY, ERROR_NO_CONTENT_TABLE));
        }
        return new RecordResponse(SUCCEED, recordMapper.fileToByteArrayDto(webhook.get()), null);
    }

    @Override
    public RecordResponse queryFile(Specification<Webhook> spec, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Webhook> filePage = webhookRepository.findAll(spec,pageable);
        return new RecordResponse(SUCCEED, recordMapper.recordPageToDtoList(filePage), null);
    }

    @Override
    public RecordResponse importDataAndSend(WebhookRequest request) {
        String errorMessage;

        try {
            /*
            File file = new File( fileDirectory+request.getFilename());
            FileSystemResource value = new FileSystemResource(file);
            HttpHeaders headers = new HttpHeaders();
            RestTemplate restTemplate = new RestTemplate();
            MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            multipartBodyBuilder.part("metadata",objectMapper.writeValueAsString(request),MediaType.APPLICATION_JSON);
            multipartBodyBuilder.part("mediaFile", value,MediaType.MULTIPART_FORM_DATA);
            // multipart/form-data request body
            MultiValueMap<String, HttpEntity<?>> multipartBody = multipartBodyBuilder.build();
            // The complete http request body.
            HttpEntity<MultiValueMap<String, HttpEntity<?>>> httpEntity = new HttpEntity<>(multipartBody, headers);
            log.info(httpEntity.toString());
            //String result = restTemplate.postForEntity(processorEndpoint, httpEntity, String.class).getBody();
            //return new RecordResponse(SUCCEED, result, null);
            */
            HttpClient httpclient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(new URI(processorEndpoint));
            httpPost.setHeader("Content-type", "multipart/form-data");
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addTextBody("metadata",objectMapper.writeValueAsString(request),ContentType.APPLICATION_JSON);
           // builder.addPart("metadata", new FileBody(new File(objectMapper.writeValueAsString(request)),ContentType.APPLICATION_JSON,"metadata"));
            httpPost.setEntity(builder.build());
            log.info("resp"+httpPost.getEntity().toString());
            HttpResponse response = httpclient.execute(httpPost);

            URL obj = new URL(processorEndpoint);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setUseCaches(false);
            con.setDoOutput(true); // indicates POST method
            con.setDoInput(true);
            con.setRequestProperty("Connection", "Keep-Alive");
            con.setRequestProperty("Cache-Control", "no-cache");
            con.setRequestProperty(
                    "Content-Type", "multipart/form-data;boundary=" + boundary);
            DataOutputStream dataOutputStream = new DataOutputStream(con.getOutputStream());

            dataOutputStream.writeBytes(twoHyphens + boundary + crlf);
            dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"" +
                   "mediaFile"+ "\";filename=\"" +
                    request.getFilename()  + "\"" + crlf);
            dataOutputStream.writeBytes(crlf);
            try (FileInputStream fis = new FileInputStream(fileDirectory+request.getFilename())) {
                byte[] buffer = new byte[64*1024];// 64 k buffer
                int len ;
                while ((len = fis.read(buffer)) > 0) {
                    dataOutputStream.write(buffer,0,len);
                }
            }
            dataOutputStream.flush();
            dataOutputStream.close();
            return new RecordResponse(SUCCEED, response.toString(), null);
        }
        catch (JsonProcessingException e) {
            log.error("JsonProcessingException on ", e);
            errorMessage = ERROR_PARSING_EXCEPTION;
        } catch (URISyntaxException e) {
            log.error("URISyntaxException on ", e);
            errorMessage = ERROR_URI_CREATING;
        } catch (IOException e) {
            errorMessage = ERROR_CONNECTION_HTTP;
            log.error("IOException on ", e);
        }
        return new RecordResponse(FAILED, "", new Error(HttpStatus.UNPROCESSABLE_ENTITY, errorMessage));
    }
}


