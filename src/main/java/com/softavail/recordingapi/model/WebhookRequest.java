package com.softavail.recordingapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class WebhookRequest {
    private String filename;
    @JsonProperty(value = "call-id")
    private String callId;
    private String from;
    private String to;
    private Long started;
    private Integer duration;
}
