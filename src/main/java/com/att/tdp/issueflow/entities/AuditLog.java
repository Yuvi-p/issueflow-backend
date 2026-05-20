package com.att.tdp.issueflow.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class AuditLog {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String action; // CREATE, UPDATE, DELETE, AUTO_ASSIGN

    @Column(name = "entity_type", nullable = false)
    private String entityType; // TICKET, PROJECT, USER

    @Column(name = "entity_id", nullable = false)
    private Long entityId;

    @Column(nullable = false)
    private String actor; // USER or SYSTEM

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performed_by")
    private User performedBy; // can be acotr if null

    @Column(nullable = false)
    private LocalDateTime timestamp;
}