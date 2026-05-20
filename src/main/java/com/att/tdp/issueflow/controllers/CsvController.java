package com.att.tdp.issueflow.controllers;

import com.att.tdp.issueflow.services.CsvService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/tickets")
@RequiredArgsConstructor
public class CsvController {

    private final CsvService csvService;

    @GetMapping("/export")
    public ResponseEntity<String> exportCsv(@RequestParam Long projectId) {
        String csvContent = csvService.exportTicketsToCsv(projectId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDisposition(ContentDisposition.attachment().filename("tickets.csv").build());
        return new ResponseEntity<>(csvContent, headers, HttpStatus.OK);
    }

    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> importCsv(
            @RequestParam("projectId") Long projectId,
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(csvService.importTicketsFromCsv(projectId, file));
    }
}