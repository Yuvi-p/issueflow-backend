package com.att.tdp.issueflow.services;

import com.att.tdp.issueflow.entities.*;
import com.att.tdp.issueflow.enums.*;
import com.att.tdp.issueflow.repositories.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.*;
import org.springframework.stereotype.Service;
import java.io.*;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CsvService {
    private final TicketRepository ticketRepository;
    private final ProjectRepository projectRepository;

    public String exportTicketsToCsv(Long projectId) {
        List<Ticket> tickets = ticketRepository.findByProjectId(projectId);
        StringWriter sw = new StringWriter();
        try (CSVPrinter printer = new CSVPrinter(sw, CSVFormat.DEFAULT.builder().setHeader("ID", "Title", "Status", "Priority").build())) {
            for (Ticket t : tickets) {
                printer.printRecord(t.getId(), t.getTitle(), t.getStatus(), t.getPriority());
            }
        } catch (IOException e) { throw new RuntimeException("CSV Export failed"); }
        return sw.toString();
    }
}