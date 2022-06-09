package com.softavail.recordingapi.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Error {
    private HttpStatus status;
    private String message;
    private String detail;
    private int httpStatusCode;

    public Error(HttpStatus status, String message, Throwable ex) {
        this.status = status;
        this.httpStatusCode = status.value();
        this.message = message;
        this.detail = ex.getMessage();
    }

    public Error(HttpStatus status, String message) {
        this.status = status;
        this.httpStatusCode = status.value();
        this.message = message;
    }
}
