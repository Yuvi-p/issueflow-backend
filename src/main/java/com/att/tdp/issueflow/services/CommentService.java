package com.att.tdp.issueflow.services;

import com.att.tdp.issueflow.entities.Comment;
import com.att.tdp.issueflow.entities.Ticket;
import com.att.tdp.issueflow.entities.User;
import com.att.tdp.issueflow.repositories.CommentRepository;
import com.att.tdp.issueflow.repositories.TicketRepository;
import com.att.tdp.issueflow.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    public List<Comment> getCommentsByTicketId(Long ticketId) {
        return commentRepository.findByTicketId(ticketId);
    }

    public List<Comment> getMentionsForUser(Long userId) {
        return commentRepository.findByMentionedUsersIdOrderByIdDesc(userId);
    }

    @Transactional
    public Comment addComment(Long ticketId, Long authorId, String content) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("Author not found"));

        Comment comment = Comment.builder()
                .ticket(ticket)
                .author(author)
                .content(content)
                .build();

        comment.setMentionedUsers(extractMentions(content));
        return commentRepository.save(comment);
    }

    @Transactional
    public Comment updateComment(Long commentId, String content) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        
        comment.setContent(content);
        // when updating a comment, the list of mentioned users is recalculated
        comment.setMentionedUsers(extractMentions(content));
        return commentRepository.save(comment);
    }

    @Transactional
    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }

    // helper function that scans the text and finds user mentions with @
    private Set<User> extractMentions(String content) {
        Set<User> mentionedUsers = new HashSet<>();
        if (content == null) return mentionedUsers;

        Pattern pattern = Pattern.compile("@(\\w+)");
        Matcher matcher = pattern.matcher(content);

        while (matcher.find()) {
            String username = matcher.group(1);
            userRepository.findByUsername(username.toLowerCase()).ifPresent(mentionedUsers::add);
        }
        return mentionedUsers;
    }
}