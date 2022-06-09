package com.softavail.recordingapi.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RecordResponse {
    private String returnCode;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timeStamp;
    private Object data;
    private Error error;

    public RecordResponse() {
        this.timeStamp = LocalDateTime.now();
    }

    public RecordResponse(String returnCode, Object data, Error error) {
        this();
        this.returnCode = returnCode;
        this.data = data;
        this.error = error;
    }

    public RecordResponse(String returnCode, LocalDateTime timeStamp, Object data, Error error) {
        this.timeStamp = timeStamp;
        this.returnCode = returnCode;
        this.data = data;
        this.error = error;
    }
}
