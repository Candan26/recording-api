package com.softavail.recordingapi.dto;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Builder
@AllArgsConstructor
public class RecordDto {
    String name;
    String type;
    String id;
    String status;
}
