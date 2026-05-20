package com.att.tdp.issueflow.services;

import com.att.tdp.issueflow.entities.*;
import com.att.tdp.issueflow.enums.*;
import com.att.tdp.issueflow.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketService {
    private final TicketRepository ticketRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final AuditLogRepository auditLogRepository;

    public List<Ticket> getTicketsByProjectId(Long projectId) {
        return ticketRepository.findByProjectId(projectId);
    }

    public Ticket getTicketById(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
    }

    @Transactional
    public Ticket createTicket(String title, String description, TicketStatus status,
                               TicketPriority priority, TicketType type, Long projectId,
                               Long assigneeId, LocalDateTime dueDate) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        User assignee = null;
        if (assigneeId != null) {
            assignee = userRepository.findById(assigneeId).orElse(null);
        } else {
            // automatic assignment if no user is assigned
            assignee = autoAssignDeveloper(projectId);
        }

        Ticket ticket = Ticket.builder()
                .title(title)
                .description(description)
                .status(status != null ? status : TicketStatus.TODO)
                .priority(priority != null ? priority : TicketPriority.LOW)
                .type(type)
                .project(project)
                .assignee(assignee)
                .dueDate(dueDate)
                .isOverdue(false)
                .isDeleted(false)
                .build();

        Ticket savedTicket = ticketRepository.save(ticket);

        // record the automatic action in the Audit Log as required
        if (assigneeId == null && assignee != null) {
            AuditLog log = AuditLog.builder()
                    .action("AUTO_ASSIGN")
                    .entityType("TICKET")
                    .entityId(savedTicket.getId())
                    .actor("SYSTEM")
                    .timestamp(LocalDateTime.now())
                    .build();
            auditLogRepository.save(log);
        }

        return savedTicket;
    }

    @Transactional
    public Ticket updateTicket(Long ticketId, String title, String description,
                               TicketStatus status, TicketPriority priority, Long assigneeId, LocalDateTime dueDate) {
        Ticket ticket = getTicketById(ticketId);

        // rule 1: a ticket in DONE status cannot be updated
        if (ticket.getStatus() == TicketStatus.DONE) {
            throw new RuntimeException("Cannot update a ticket that is already DONE");
        }

        // rule 2: validate status transitions forward only
        if (status != null) {
            validateStatusTransition(ticket.getStatus(), status);

            if (status == TicketStatus.DONE && hasUnresolvedBlockers(ticket)) {
            throw new RuntimeException("Cannot transition to DONE: Ticket has unresolved blockers");
        }
            ticket.setStatus(status);
        }

        if (title != null) ticket.setTitle(title);
        if (description != null) ticket.setDescription(description);
        
        if (priority != null) {
            ticket.setPriority(priority);
            ticket.setOverdue(false); // manual priority change resets the overdue flag
        }
        
        if (assigneeId != null) {
            User newAssignee = userRepository.findById(assigneeId).orElse(null);
            ticket.setAssignee(newAssignee);
        }
        
        if (dueDate != null) ticket.setDueDate(dueDate);

        return ticketRepository.save(ticket);
    }

    @Transactional
    public void softDeleteTicket(Long ticketId) {
        Ticket ticket = getTicketById(ticketId);
        ticket.setDeleted(true);
        ticketRepository.save(ticket);
    }

    // helper function for validating status transitions
    private void validateStatusTransition(TicketStatus current, TicketStatus next) {
        if (current == next) return;
        boolean valid = false;
        if (current == TicketStatus.TODO && (next == TicketStatus.IN_PROGRESS || next == TicketStatus.DONE)) valid = true;
        if (current == TicketStatus.IN_PROGRESS && (next == TicketStatus.IN_REVIEW || next == TicketStatus.DONE)) valid = true;
        if (current == TicketStatus.IN_REVIEW && next == TicketStatus.DONE) valid = true;

        if (!valid) {
            throw new RuntimeException("Invalid status transition from " + current + " to " + next);
        }
    }

    // helper function for automatic assignment
    private User autoAssignDeveloper(Long projectId) {
        List<User> developers = userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.DEVELOPER)
                .toList();

        if (developers.isEmpty()) return null;

        // Workload calculation: Finding the developer with the fewest open tickets, breaking ties by ID (oldest)
        return developers.stream().min(Comparator.comparingLong((User dev) ->
                ticketRepository.findByProjectId(projectId).stream()
                        .filter(t -> t.getAssignee() != null && t.getAssignee().getId().equals(dev.getId()))
                        .filter(t -> t.getStatus() != TicketStatus.DONE)
                        .count()
        ).thenComparing(User::getId)).orElse(null);
    }

    @Transactional
    public void addDependency(Long ticketId, Long blockerId) {
        Ticket ticket = getTicketById(ticketId);
        Ticket blocker = getTicketById(blockerId);
        
        if (!ticket.getProject().getId().equals(blocker.getProject().getId())) {
            throw new RuntimeException("Tickets must belong to the same project");
        }
        
        ticket.getBlockedBy().add(blocker);
        ticketRepository.save(ticket);
    }

    @Transactional
    public void removeDependency(Long ticketId, Long blockerId) {
        Ticket ticket = getTicketById(ticketId);
        ticket.getBlockedBy().removeIf(b -> b.getId().equals(blockerId));
        ticketRepository.save(ticket);
    }

    public java.util.Set<Ticket> getDependencies(Long ticketId) {
        return getTicketById(ticketId).getBlockedBy();
    }

    // helper function to check if there are unresolved blockers
    private boolean hasUnresolvedBlockers(Ticket ticket) {
        if (ticket.getBlockedBy() == null) return false;
        return ticket.getBlockedBy().stream()
                .anyMatch(blocker -> blocker.getStatus() != TicketStatus.DONE);
    }
    
    public List<Ticket> getSoftDeletedTickets(Long projectId) {
        return ticketRepository.findSoftDeletedTickets(projectId);
    }

    @org.springframework.transaction.annotation.Transactional
    public Ticket restoreTicket(Long id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
        ticket.setDeleted(false);
        return ticketRepository.save(ticket);
    }
}