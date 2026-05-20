package com.att.tdp.issueflow.controllers;

import com.att.tdp.issueflow.entities.Comment;
import com.att.tdp.issueflow.services.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tickets/{ticketId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping
    public ResponseEntity<List<Comment>> getComments(@PathVariable Long ticketId) {
        return ResponseEntity.ok(commentService.getCommentsByTicketId(ticketId));
    }

    @PostMapping
    public ResponseEntity<Comment> addComment(
            @PathVariable Long ticketId, 
            @RequestBody CommentRequest request) {
        return ResponseEntity.ok(commentService.addComment(ticketId, request.authorId(), request.content()));
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<Comment> updateComment(
            @PathVariable Long ticketId, 
            @PathVariable Long commentId, 
            @RequestBody CommentRequest request) {
        return ResponseEntity.ok(commentService.updateComment(commentId, request.content()));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long ticketId, 
            @PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.ok().build();
    }

    public record CommentRequest(Long authorId, String content) {}
}