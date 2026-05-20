package com.att.tdp.issueflow.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "attachments")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Attachment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String filename;

    @Column(name = "content_type", nullable = false)
    private String contentType;

    @Lob // large object for binary data
    @Column(nullable = false)
    private byte[] data;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;
}