package com.softavail.recordingapi.service;

import com.softavail.recordingapi.entity.Webhook;
import com.softavail.recordingapi.model.RecordResponse;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.multipart.MultipartFile;

public interface RecordService {
    RecordResponse uploadFile(MultipartFile multipartFile, Webhook request);

    RecordResponse deleteFile(String id);

    RecordResponse updateFile(MultipartFile multipartFile, Webhook request);

    RecordResponse getAllFileInfo();

    RecordResponse getFileById(String id);

    RecordResponse queryFile(Specification<Webhook> spec, int page, int size);
}
