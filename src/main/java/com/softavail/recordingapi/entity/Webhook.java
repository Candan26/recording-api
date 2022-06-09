package com.softavail.recordingapi.entity;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Webhook {
    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    private String id;

    @Column
    private String filename;

    @Column(nullable=false ,name = "call_id")
    private String callId;

    @Column(name = "file_from")
    private String from;

    @Column
    private String to;

    @Column
    private Long started;

    @Column
    private Integer duration;

    @Column
    private String status;

    @Lob
    @Column(name = "content", length = 5000000)
    private byte[] content;

    @Column
    private String fileExtension;
}
