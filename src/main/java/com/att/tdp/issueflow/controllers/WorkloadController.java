package com.att.tdp.issueflow.controllers;

import com.att.tdp.issueflow.enums.Role;
import com.att.tdp.issueflow.enums.TicketStatus;
import com.att.tdp.issueflow.repositories.TicketRepository;
import com.att.tdp.issueflow.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/projects/{projectId}/workload")
@RequiredArgsConstructor
public class WorkloadController {
    
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<Map<String, Long>> getProjectWorkload(@PathVariable Long projectId) {
        Map<String, Long> workload = new HashMap<>();
        
        userRepository.findAll().stream()
            .filter(u -> u.getRole() == Role.DEVELOPER)
            .forEach(dev -> {
                long openTickets = ticketRepository.findByProjectId(projectId).stream()
                    .filter(t -> t.getAssignee() != null && t.getAssignee().getId().equals(dev.getId()))
                    .filter(t -> t.getStatus() != TicketStatus.DONE)
                    .count();
                workload.put(dev.getUsername(), openTickets);
            });
            
        return ResponseEntity.ok(workload);
    }
}