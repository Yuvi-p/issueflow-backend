package com.att.tdp.issueflow.services;

import com.att.tdp.issueflow.entities.Ticket;
import com.att.tdp.issueflow.enums.TicketPriority;
import com.att.tdp.issueflow.enums.TicketStatus;
import com.att.tdp.issueflow.repositories.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EscalationScheduler {

    private final TicketRepository ticketRepository;

    // runs automatically every day at midnight
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void escalateOverdueTickets() {
        List<Ticket> allTickets = ticketRepository.findAll();
        LocalDateTime now = LocalDateTime.now();

        for (Ticket ticket : allTickets) {
            if (ticket.getStatus() != TicketStatus.DONE && 
                ticket.getDueDate() != null && 
                ticket.getDueDate().isBefore(now) && 
                !ticket.isOverdue()) {
                
                ticket.setOverdue(true);
                
                // increase priority by one level
                if (ticket.getPriority() == TicketPriority.LOW) ticket.setPriority(TicketPriority.MEDIUM);
                else if (ticket.getPriority() == TicketPriority.MEDIUM) ticket.setPriority(TicketPriority.HIGH);
                else if (ticket.getPriority() == TicketPriority.HIGH) ticket.setPriority(TicketPriority.CRITICAL);
                
                ticketRepository.save(ticket);
            }
        }
    }
}