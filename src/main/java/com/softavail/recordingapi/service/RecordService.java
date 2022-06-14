package com.softavail.recordingapi.service;

import com.softavail.recordingapi.entity.Webhook;
import com.softavail.recordingapi.model.RecordResponse;
import com.softavail.recordingapi.model.WebhookRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.multipart.MultipartFile;

public interface RecordService {
    RecordResponse uploadFile(MultipartFile multipartFile, Webhook request);

    RecordResponse deleteFile(Webhook id);

    RecordResponse updateFile(MultipartFile multipartFile, Webhook request);

    RecordResponse getAllFileInfo();

    RecordResponse getFileByCallId(String callId);

    RecordResponse getFileById(String id);

    RecordResponse queryFile(Specification<Webhook> spec, int page, int size);

    RecordResponse importDataAndSend(WebhookRequest request);
}
