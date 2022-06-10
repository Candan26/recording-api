package com.softavail.recordingapi.service.impl;

import com.softavail.recordingapi.entity.Webhook;
import com.softavail.recordingapi.mapper.RecordMapper;
import com.softavail.recordingapi.model.Error;
import com.softavail.recordingapi.model.RecordResponse;
import com.softavail.recordingapi.repository.WebhookRepository;
import com.softavail.recordingapi.service.RecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.softavail.recordingapi.util.RecordingUtil.*;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class RecordServiceImpl implements RecordService {

    private final WebhookRepository webhookRepository;

    private final RecordMapper recordMapper;

    @Value("#{'${extension.list}'.split(',')}")
     private List<String> EXTENSION_LIST;

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
}


