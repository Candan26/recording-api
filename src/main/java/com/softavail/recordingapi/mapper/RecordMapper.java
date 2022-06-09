package com.softavail.recordingapi.mapper;

import com.softavail.recordingapi.dto.ByteArrayDto;
import com.softavail.recordingapi.dto.RecordDto;
import com.softavail.recordingapi.dto.RecordPaginationDto;
import com.softavail.recordingapi.entity.Webhook;
import org.springframework.data.domain.Page;

import java.util.List;

public interface RecordMapper {

    RecordDto recordToDto(Webhook webhookSaved);

    RecordDto deleteRequestToDto(String id, String status);

    List<?> recordListToDtoList(List<Webhook> webhookList);

    Page<RecordPaginationDto> recordPageToDtoList(Page<Webhook> filePage);

    ByteArrayDto fileToByteArrayDto(Webhook webhook);
}
