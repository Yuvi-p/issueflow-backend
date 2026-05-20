package com.att.tdp.issueflow.repositories;

import com.att.tdp.issueflow.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByTicketId(Long ticketId);

    // finding all comments where a specific user is mentioned, sorted from the end to the beginning
    List<Comment> findByMentionedUsersIdOrderByIdDesc(Long userId);
}