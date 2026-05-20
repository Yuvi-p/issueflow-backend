package com.att.tdp.issueflow.controllers;

import com.att.tdp.issueflow.services.CsvService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/projects/{projectId}/csv")
@RequiredArgsConstructor
public class CsvController {

    private final CsvService csvService;

    @GetMapping
    public ResponseEntity<String> exportCsv(@PathVariable Long projectId) {
        String csvContent = csvService.exportTicketsToCsv(projectId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDisposition(ContentDisposition.attachment().filename("tickets.csv").build());
        return new ResponseEntity<>(csvContent, headers, HttpStatus.OK);
    }
}