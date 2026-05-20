package com.att.tdp.issueflow.services;

import com.att.tdp.issueflow.entities.Attachment;
import com.att.tdp.issueflow.entities.Ticket;
import com.att.tdp.issueflow.repositories.AttachmentRepository;
import com.att.tdp.issueflow.repositories.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final TicketRepository ticketRepository;

    public List<Attachment> getAttachmentsByTicketId(Long ticketId) {
        return attachmentRepository.findByTicketId(ticketId);
    }

    @Transactional
    public Attachment uploadAttachment(Long ticketId, MultipartFile file) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        // validation of file type according to requirements
        String contentType = file.getContentType();
        if (contentType == null || !(contentType.equals("image/png") || 
            contentType.equals("image/jpeg") || 
            contentType.equals("application/pdf") || 
            contentType.equals("text/plain"))) {
            throw new RuntimeException("Invalid file type. Only PNG, JPEG, PDF, and plain text are allowed.");
        }

        // limit of 10MB is automatically enforced at the application.yaml level provided to you

        try {
            Attachment attachment = Attachment.builder()
                    .filename(file.getOriginalFilename())
                    .contentType(contentType)
                    .data(file.getBytes())
                    .ticket(ticket)
                    .build();

            return attachmentRepository.save(attachment);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file data", e);
        }
    }

    @Transactional
    public void deleteAttachment(Long attachmentId) {
        attachmentRepository.deleteById(attachmentId);
    }
}