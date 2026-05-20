package com.att.tdp.issueflow.controllers;

import com.att.tdp.issueflow.entities.Ticket;
import com.att.tdp.issueflow.services.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/tickets/{ticketId}/dependencies")
@RequiredArgsConstructor
public class TicketDependencyController {

    private final TicketService ticketService;

    @PostMapping
    public ResponseEntity<Void> addDependency(@PathVariable Long ticketId, @RequestBody Map<String, Long> request) {
        ticketService.addDependency(ticketId, request.get("blockedBy"));
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Set<Ticket>> getDependencies(@PathVariable Long ticketId) {
        return ResponseEntity.ok(ticketService.getDependencies(ticketId));
    }

    @DeleteMapping("/{blockerId}")
    public ResponseEntity<Void> removeDependency(@PathVariable Long ticketId, @PathVariable Long blockerId) {
        ticketService.removeDependency(ticketId, blockerId);
        return ResponseEntity.ok().build();
    }
}