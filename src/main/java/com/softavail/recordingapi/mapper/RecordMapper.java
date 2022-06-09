package com.softavail.recordingapi.mapper;

import com.softavail.recordingapi.dto.RecordDto;
import com.softavail.recordingapi.entity.Webhook;

import java.util.List;

public interface RecordMapper {

    RecordDto recordToDto(Webhook webhookSaved);

    RecordDto deleteRequestToDto(String id, String status);

    List<?> recordListToDtoList(List<Webhook> webhookList);
}
