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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

import static com.softavail.recordingapi.util.RecordingUtil.EXTENSION_LIST;
import static com.softavail.recordingapi.util.RecordingUtil.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class RecordServiceImpl implements RecordService {

    private final WebhookRepository webhookRepository;

    private final RecordMapper recordMapper;

    @Override
    public RecordResponse uploadFile(MultipartFile mf, Webhook request) {
        try {
            if (!Arrays.asList(EXTENSION_LIST).contains(FilenameUtils.getExtension(mf.getOriginalFilename()))) {
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
    public RecordResponse deleteFile(String id) {
        try {
            if (id != null) {
                webhookRepository.deleteAllByCallId(id);
                return new RecordResponse(SUCCEED, recordMapper.deleteRequestToDto(id, DELETED), null);
            } else {
                log.error("Please add a file name or id in request file");
                return new RecordResponse(FAILED, "", new Error(HttpStatus.UNPROCESSABLE_ENTITY, ERROR_UNPROCESSABLE_FILE_BRACE));
            }
        } catch (Exception ex) {
            log.error("Exception on ", ex);
            return new RecordResponse(FAILED, "", new Error(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), ex));
        }

    }

    @Override
    public RecordResponse updateFile(MultipartFile multipartFile, Webhook request) {
        return null;
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
    public RecordResponse getFileById(String id) {
        return null;
    }

    @Override
    public RecordResponse queryFile(Specification<Webhook> spec, int page, int size) {
        return null;
    }
}
