package com.att.tdp.issueflow.controllers;

import com.att.tdp.issueflow.entities.Ticket;
import com.att.tdp.issueflow.enums.TicketPriority;
import com.att.tdp.issueflow.enums.TicketStatus;
import com.att.tdp.issueflow.enums.TicketType;
import com.att.tdp.issueflow.services.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @GetMapping
    public ResponseEntity<List<Ticket>> getTicketsByProject(@RequestParam Long projectId) {
        return ResponseEntity.ok(ticketService.getTicketsByProjectId(projectId));
    }

    @GetMapping("/{ticketId}")
    public ResponseEntity<Ticket> getTicketById(@PathVariable Long ticketId) {
        return ResponseEntity.ok(ticketService.getTicketById(ticketId));
    }

    @PostMapping
    public ResponseEntity<Ticket> createTicket(@RequestBody CreateTicketRequest request) {
        Ticket created = ticketService.createTicket(
                request.title(), request.description(), request.status(),
                request.priority(), request.type(), request.projectId(),
                request.assigneeId(), request.dueDate()
        );
        return ResponseEntity.ok(created);
    }

    @PatchMapping("/{ticketId}")
    public ResponseEntity<Ticket> updateTicket(
            @PathVariable Long ticketId,
            @RequestBody UpdateTicketRequest request) {
        Ticket updated = ticketService.updateTicket(
                ticketId, request.title(), request.description(),
                request.status(), request.priority(), request.assigneeId(), request.dueDate()
        );
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{ticketId}")
    public ResponseEntity<Void> softDeleteTicket(@PathVariable Long ticketId) {
        ticketService.softDeleteTicket(ticketId);
        return ResponseEntity.ok().build();
    }

    public record CreateTicketRequest(String title, String description, TicketStatus status,
                                      TicketPriority priority, TicketType type, Long projectId,
                                      Long assigneeId, LocalDateTime dueDate) {}

    public record UpdateTicketRequest(String title, String description, TicketStatus status,
                                      TicketPriority priority, Long assigneeId, LocalDateTime dueDate) {}
    
    @GetMapping("/deleted")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Ticket>> getDeletedTickets(@RequestParam Long projectId) {
        return ResponseEntity.ok(ticketService.getSoftDeletedTickets(projectId));
    }

    @PostMapping("/{id}/restore")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Ticket> restoreTicket(@PathVariable Long id) {
        return ResponseEntity.ok(ticketService.restoreTicket(id));
    
    }
}