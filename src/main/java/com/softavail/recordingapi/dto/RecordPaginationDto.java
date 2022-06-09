package com.softavail.recordingapi.dto;

import lombok.*;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Builder
@AllArgsConstructor
public class RecordPaginationDto {
    private String id;
    private String fileName;
    private String callId;
    private String from;
    private String to;
    private  Long started;
    private byte[] content;
    private  Integer duration;
    private String status;
}
