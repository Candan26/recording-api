package com.softavail.recordingapi.dto;

import lombok.*;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Builder
@AllArgsConstructor
public class ByteArrayDto {
    String callId;
    String id;
    String fileName;
    String from;
    String to;
    Long started;
    Integer duration;
    private byte[] content;
}
