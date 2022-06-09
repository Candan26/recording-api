package com.softavail.recordingapi.mapper.impl;

import com.softavail.recordingapi.dto.ByteArrayDto;
import com.softavail.recordingapi.dto.RecordDto;
import com.softavail.recordingapi.entity.Webhook;
import com.softavail.recordingapi.mapper.RecordMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class RecordMapperImpl implements RecordMapper {
    @Override
    public RecordDto recordToDto(Webhook webhookSaved) {
        return RecordDto.builder().status(webhookSaved.getStatus()).id(webhookSaved.getCallId()).name(webhookSaved.getFilename())
                .type(webhookSaved.getFileExtension()).build();
    }

    @Override
    public RecordDto deleteRequestToDto(String id, String status) {
        return RecordDto.builder().status(status).id(id).build();
    }

    @Override
    public List<ByteArrayDto> recordListToDtoList(List<Webhook> webhookList) {
        List<ByteArrayDto> recordDtoList = new ArrayList<>();
        webhookList.forEach((v) -> {
            ByteArrayDto byteArrayDto = ByteArrayDto.builder().
                    callId(v.getCallId()).
                    fileName(v.getFilename()).
                    from(v.getFrom()).
                    to(v.getTo()).
                    started(v.getStarted()).
                    duration(v.getDuration()).
                    content(v.getContent()).
                    build();
            recordDtoList.add(byteArrayDto);
        });
        return recordDtoList;
    }
}
