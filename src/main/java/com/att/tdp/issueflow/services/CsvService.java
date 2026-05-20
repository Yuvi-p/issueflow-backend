package com.att.tdp.issueflow.services;

import com.att.tdp.issueflow.entities.*;
import com.att.tdp.issueflow.enums.*;
import com.att.tdp.issueflow.repositories.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CsvService {
    private final TicketRepository ticketRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public String exportTicketsToCsv(Long projectId) {
        List<Ticket> tickets = ticketRepository.findByProjectId(projectId);
        StringWriter sw = new StringWriter();
        try (CSVPrinter printer = new CSVPrinter(sw, CSVFormat.DEFAULT.builder()
                .setHeader("ID", "Title", "Description", "Status", "Priority", "Type", "AssigneeId").build())) {
            
            for (Ticket t : tickets) {
                printer.printRecord(
                    t.getId(), 
                    t.getTitle(), 
                    t.getDescription(), 
                    t.getStatus(), 
                    t.getPriority(), 
                    t.getType(), 
                    t.getAssignee() != null ? t.getAssignee().getId() : ""
                );
            }
            
        } catch (IOException e) { 
            throw new RuntimeException("CSV Export failed", e); 
        }
        return sw.toString();
    }

    public Map<String, Object> importTicketsFromCsv(Long projectId, MultipartFile file) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        int created = 0;
        int failed = 0;
        List<String> errors = new ArrayList<>();

        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
             CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.builder()
                     .setHeader()
                     .setSkipHeaderRecord(true)
                     .setIgnoreHeaderCase(true)
                     .setTrim(true)
                     .build())) {

            for (CSVRecord csvRecord : csvParser) {
                try {
                    String title = csvRecord.get("Title");
                    String description = csvRecord.get("Description");
                    TicketStatus status = TicketStatus.valueOf(csvRecord.get("Status").toUpperCase());
                    TicketPriority priority = TicketPriority.valueOf(csvRecord.get("Priority").toUpperCase());
                    TicketType type = TicketType.valueOf(csvRecord.get("Type").toUpperCase());
                    
                    String assigneeIdStr = csvRecord.get("AssigneeId");
                    User assignee = null;
                    if (assigneeIdStr != null && !assigneeIdStr.isEmpty()) {
                        assignee = userRepository.findById(Long.parseLong(assigneeIdStr)).orElse(null);
                    }

                    Ticket ticket = Ticket.builder()
                            .title(title)
                            .description(description)
                            .status(status)
                            .priority(priority)
                            .type(type)
                            .project(project)
                            .assignee(assignee)
                            .dueDate(LocalDateTime.now().plusDays(7))
                            .build();

                    ticketRepository.save(ticket);
                    created++;
                } catch (Exception e) {
                    failed++;
                    errors.add("Row " + csvRecord.getRecordNumber() + " failed: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse CSV file", e);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("created", created);
        response.put("failed", failed);
        response.put("errors", errors);
        return response;
    }
}